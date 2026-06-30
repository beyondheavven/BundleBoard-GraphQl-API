package com.source.bundleboard.email.service;

import com.source.bundleboard.api.exception.InvalidEmailVerificationTokenException;
import com.source.bundleboard.api.exception.UserNotFoundException;
import com.source.bundleboard.api.exception.UserStatusException;
import com.source.bundleboard.email.dto.EmailResponse;
import com.source.bundleboard.email.dto.TokenEntity;
import com.source.bundleboard.email.mail.propeties.MailProperties;
import com.source.bundleboard.email.model.EmailVerificationToken;
import com.source.bundleboard.email.model.EmailTokenStatus;
import com.source.bundleboard.email.model.EmailTokenType;
import com.source.bundleboard.email.properties.EmailVerificationProperties;
import com.source.bundleboard.email.repository.EmailVerificationTokenRepository;
import com.source.bundleboard.rabbitmq.dto.EmailTask;
import com.source.bundleboard.rabbitmq.producer.TaskProducer;
import com.source.bundleboard.user.model.User;
import com.source.bundleboard.user.model.UserStatus;
import com.source.bundleboard.user.service.UserService;
import com.source.bundleboard.utils.AppLinkBuilder;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationTokenServiceImpl implements EmailVerificationTokenService {

    private final EmailVerificationTokenRepository emailVerificationTokenRepository;

    private final EmailVerificationProperties emailVerificationProperties;

    private final UserService userService;

    private final TaskProducer taskProducer;

    private final MailProperties mailProperties;

    private final AppLinkBuilder appLinkBuilder;

    private final SecureRandom random = new SecureRandom();


    @Override
    @Transactional
    public Mono<EmailResponse> verifyEmail(String tokenValue) {
        // 1. Логируем входящий токен
        log.info("🔍 [DEBUG] Step 1: Received raw token from frontend. Length: {}",
                tokenValue != null ? tokenValue.length() : "NULL");

        if (tokenValue == null || tokenValue.trim().isEmpty()) {
            log.error("❌ [DEBUG] Verification aborted: incoming token value is empty.");
            return Mono.error(new InvalidEmailVerificationTokenException());
        }

        String hashedToken = sha256Hex(tokenValue);
        log.info("🔍 [DEBUG] Step 2: Hashed incoming token to SHA-256: {}", hashedToken);

        return emailVerificationTokenRepository.findByToken(hashedToken)
                // Логируем, если токен вообще был найден в БД
                .doOnNext(token -> log.info("🟢 [DEBUG] Step 3: Token found in Database. ID: {}, UserID: {}, Status: {}, ExpiresAt: {}",
                        token.getId(), token.getUserId(), token.getEmailTokenStatus(), token.getExpiresAt()))
                .flatMap(token -> {
                    // Проверка блокировки попыток
                    if (token.getBlockedUntil() != null && token.getBlockedUntil().isAfter(Instant.now())) {
                        log.warn("⛔ [DEBUG] Verification blocked: Token is currently rate-limited until {}", token.getBlockedUntil());
                        return Mono.error(new UserStatusException());
                    }

                    // Проверка валидности
                    boolean isValid = token.getEmailTokenStatus() == EmailTokenStatus.pending &&
                            token.getExpiresAt().isAfter(Instant.now());

                    if (!isValid) {
                        log.warn("🟡 [DEBUG] Token is invalid (Either not pending or expired). Handling failed attempt...");
                        return handleFailedAttempt(token);
                    }

                    log.info("🔍 [DEBUG] Step 4: Token is valid. Requesting User profile from UserService for ID: {}", token.getUserId());

                    // ТУТ НАЧИНАЕТСЯ ЗОНА РИСКА SPRING SECURITY
                    return userService.getUserById(token.getUserId())
                            .doOnSubscribe(sub -> log.info("🔍 [DEBUG] Subscribed to userService.getUserById()"))
                            .doOnNext(user -> log.info("🟢 [DEBUG] Step 5: User profile retrieved successfully. Current Status: {}", user.getStatus()))
                            .doOnError(e -> log.error("🔴 [DEBUG] CRITICAL: userService.getUserById() failed! Error type: {}, Message: {}",
                                    e.getClass().getName(), e.getMessage()))

                            // Переходим к обновлению пользователя
                            .flatMap(user -> {
                                log.info("🔍 [DEBUG] Step 6: Triggering processUserUpdate (modifying status to ACTIVE)...");
                                return processUserUpdate(user, token);
                            })
                            .doOnSubscribe(sub -> log.info("🔍 [DEBUG] Subscribed to processUserUpdate()"))
                            .doOnNext(updatedUser -> log.info("🟢 [DEBUG] Step 7: User status updated and saved. New Status: {}", updatedUser.getStatus()))
                            .doOnError(e -> log.error("🔴 [DEBUG] CRITICAL: processUserUpdate() database write failed! Error type: {}, Message: {}",
                                    e.getClass().getName(), e.getMessage()))

                            // Переходим к обновлению статуса самого токена
                            .flatMap(user -> {
                                log.info("🔍 [DEBUG] Step 8: Changing token status to VERIFIED...");
                                token.setEmailTokenStatus(EmailTokenStatus.verified);
                                return emailVerificationTokenRepository.save(token);
                            })
                            .doOnNext(savedToken -> log.info("🟢 [DEBUG] Step 9: Token marked as verified in DB."))
                            .thenReturn(new EmailResponse(true, "Email verified successfully", null));
                })
                // Если репозиторий вернул пустой Mono (хэш не найден в таблице)
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("❌ [DEBUG] Step 3/FAIL: No record matching hashed token {} found in the database.", hashedToken);
                    return Mono.error(new InvalidEmailVerificationTokenException());
                }))
                // Перехватываем абсолютно любую ошибку во всем реактивном стриме
                .doOnError(globalError -> log.error("🚨 [DEBUG] GLOBAL STREAM ERROR inside verifyEmail: {} - {}",
                        globalError.getClass().getName(), globalError.getMessage()));
    }

//    @Override
//    @Transactional
//    public Mono<EmailResponse> verifyEmail(String tokenValue) {
//        String hashedToken = sha256Hex(tokenValue);
//        return emailVerificationTokenRepository.findByToken(hashedToken)
//                .flatMap(token -> {
//                    if (token.getBlockedUntil() != null && token.getBlockedUntil().isAfter(Instant.now())) {
//                        return Mono.error(new UserStatusException());
//                    }
//
//                    boolean isValid = token.getEmailTokenStatus() == EmailTokenStatus.pending &&
//                            token.getExpiresAt().isAfter(Instant.now());
//
//                    if (!isValid) {
//                        return handleFailedAttempt(token);
//                    }
//
//                    return userService.getUserById(token.getUserId())
//                            .flatMap(user -> processUserUpdate(user, token))
//                            .flatMap(user -> {
//                                token.setEmailTokenStatus(EmailTokenStatus.verified);
//                                return emailVerificationTokenRepository.save(token);
//                            })
//                            .thenReturn(new EmailResponse(true, "Email verified successfully", null));
//                }).switchIfEmpty(Mono.error(new InvalidEmailVerificationTokenException()));
//    }

    @Override
    @Transactional
    public Mono<EmailResponse> sendChangeEmailToken(String newEmail, String username) {
        return userService.getUserByUsername(username)
                .flatMap(user -> {
                    TokenEntity tokenEntity = createTokenEntity(user.getId(), EmailTokenType.change_email);
                    tokenEntity.token().setNewEmail(newEmail);

                    return getMono(newEmail, tokenEntity);
                });
    }

    @Override
    @Transactional
    public Mono<EmailResponse> resendVerificationEmail(String email) {
        return userService.getUserByEmail(email)
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .flatMap(user -> {
                    TokenEntity tokenEntity = createTokenEntity(user.getId(), EmailTokenType.verify_email);

                    return getMono(email, tokenEntity);
                });
    }

    @NotNull
    private Mono<EmailResponse> getMono(String email, TokenEntity tokenEntity) {
        return emailVerificationTokenRepository.save(tokenEntity.token())
                .flatMap(token -> {
                    String link = appLinkBuilder.buildLink(mailProperties.getPaths().getVerificationEmail(), tokenEntity.rawToken());
                    EmailTask task = new EmailTask(
                            email,
                            mailProperties.getSubjects().getVerificationEmail(),
                            mailProperties.getTemplates().getVerificationEmail(),
                            Map.of("verificationLink", link)
                    );
                    return taskProducer.sendEmailTask(task);
                })
                .thenReturn(new EmailResponse(true, "Email verification link sent to " + email, null));
    }

    private TokenEntity createTokenEntity(Long userId, EmailTokenType emailTokenType) {
        String rawToken = generateRawToken();
        String hashedToken = sha256Hex(rawToken);

        EmailVerificationToken token = new EmailVerificationToken();
        token.setUserId(userId);
        token.setToken(hashedToken);
        token.setEmailTokenType(emailTokenType);
        token.setEmailTokenStatus(EmailTokenStatus.pending);
        token.setCreatedAt(Instant.now());
        token.setExpiresAt(Instant.now().plusSeconds(3600));
        token.setAttemptCount(0);
        token.setResendCount(0);
        return new TokenEntity(rawToken, token);
    }

    @Override
    public String generateRawToken() {
        byte[] tokenBytes = new byte[emailVerificationProperties.getByteLength()];
        random.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    @Override
    public String sha256Hex(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance(emailVerificationProperties.getHashAlgorithm());
            byte[] digest = md.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(emailVerificationProperties.getHashLength());
            for (byte b : digest) {
                String format = emailVerificationProperties.getHexFormat();
                sb.append(String.format(format, b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }

    private Mono<User> processUserUpdate(User user, EmailVerificationToken token) {
        if (token.getEmailTokenType() == EmailTokenType.change_email && token.getNewEmail() != null) {
            user.setEmail(token.getNewEmail());
        }
        user.setStatus(UserStatus.active);
        return userService.save(user);
    }

    private Mono<EmailResponse> handleFailedAttempt(EmailVerificationToken token) {
        token.setAttemptCount(token.getAttemptCount() + 1);
        int max = emailVerificationProperties.getMaxAttempts();
        int left = max - token.getAttemptCount();

        if(left <= 0){
            token.setBlockedUntil(Instant.now().plusSeconds(emailVerificationProperties.getBlockDurationSeconds()));
        }

        return emailVerificationTokenRepository.save(token)
                .flatMap(savedToken -> Mono.error(new InvalidEmailVerificationTokenException()));
    }

}
