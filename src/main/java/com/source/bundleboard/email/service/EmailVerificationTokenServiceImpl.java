package com.source.bundleboard.email.service;

import com.source.bundleboard.api.exception.InvalidEmailVerificationTokenException;
import com.source.bundleboard.api.exception.UserNotFoundException;
import com.source.bundleboard.email.dto.TokenEntity;
import com.source.bundleboard.email.mail.service.MailService;
import com.source.bundleboard.email.model.EmailVerificationToken;
import com.source.bundleboard.email.model.TokenStatus;
import com.source.bundleboard.email.model.TokenType;
import com.source.bundleboard.email.properties.EmailVerificationProperties;
import com.source.bundleboard.email.repository.EmailVerificationTokenRepository;
import com.source.bundleboard.user.model.User;
import com.source.bundleboard.user.model.UserStatus;
import com.source.bundleboard.user.service.UserService;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationTokenServiceImpl implements EmailVerificationTokenService {

    private final EmailVerificationTokenRepository emailVerificationTokenRepository;

    private final EmailVerificationProperties emailVerificationProperties;

    private final UserService userService;

    private final MailService mailService;

    private final SecureRandom random = new SecureRandom();

    @Override
    @Transactional
    public Mono<Boolean> verifyEmail(String tokenValue) {
        String hashedToken = sha256Hex(tokenValue);
        log.debug("Attempting to verify email with token hash: {}", hashedToken);
        return emailVerificationTokenRepository.findByToken(hashedToken)
                .filter(token -> token.getTokenStatus() == TokenStatus.pending && token.getExpiresAt().isAfter(Instant.now()))
                .switchIfEmpty(Mono.error(new InvalidEmailVerificationTokenException()))
                .flatMap(token -> userService.getUserById(token.getUserId())
                        .flatMap(user -> processUserUpdate(user, token))
                        .flatMap(user -> {
                            token.setTokenStatus(TokenStatus.verified);
                            return emailVerificationTokenRepository.save(token);
                        })
                        .thenReturn(true))
                .doOnSuccess(s -> log.info("Email verified and token deleted for hash: {}", hashedToken));
    }

    @Override
    public Mono<Boolean> sendChangeEmailToken(String newEmail, String currentEmail) {
        return userService.getUserByEmail(currentEmail)
                .flatMap(user -> {
                    TokenEntity tokenEntity = createTokenEntity(user.getId(), TokenType.change_email);
                    tokenEntity.token().setNewEmail(newEmail);

                    return emailVerificationTokenRepository.save(tokenEntity.token())
                            .flatMap(token -> mailService.sendVerificationEmail(newEmail, tokenEntity.rawToken()))
                            .thenReturn(true);
                });
    }

    @Override
    public Mono<Boolean> resendVerificationEmail(String email) {
        return userService.getUserByEmail(email)
                .filter(user -> user.getStatus() == UserStatus.inactive)
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .flatMap(user -> {
                    TokenEntity tokenEntity = createTokenEntity(user.getId(), TokenType.verify_email);

                    return emailVerificationTokenRepository.save(tokenEntity.token())
                            .flatMap(token -> mailService.sendVerificationEmail(email, tokenEntity.rawToken()))
                            .thenReturn(true);
                });
    }

    private TokenEntity createTokenEntity(Long userId, TokenType tokenType) {
        String rawToken = generateRawToken();
        String hashedToken = sha256Hex(rawToken);

        EmailVerificationToken token = new EmailVerificationToken();
        token.setUserId(userId);
        token.setToken(hashedToken);
        token.setTokenType(tokenType);
        token.setTokenStatus(TokenStatus.pending);
        token.setCreatedAt(Instant.now());
        token.setExpiresAt(Instant.now().plusSeconds(3600));
        return new TokenEntity(rawToken, token);
    }

    private Mono<User> processUserUpdate(User user, EmailVerificationToken token) {
        if(token.getTokenType() == TokenType.change_email && token.getNewEmail() != null){
            user.setEmail(token.getNewEmail());
        }
        user.setStatus(UserStatus.active);
        return userService.saveUser(user);
    }

    private String generateRawToken() {
        byte[] tokenBytes = new byte[emailVerificationProperties.getByteLength()];
        random.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    private String sha256Hex(String value) {
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

}
