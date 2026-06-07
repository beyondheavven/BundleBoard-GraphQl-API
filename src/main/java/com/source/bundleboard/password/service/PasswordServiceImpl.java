package com.source.bundleboard.password.service;

import com.source.bundleboard.api.exception.InvalidTokenException;
import com.source.bundleboard.api.exception.UnmatchedPasswordsException;
import com.source.bundleboard.api.exception.UserStatusException;
import com.source.bundleboard.email.mail.propeties.MailProperties;
import com.source.bundleboard.email.service.EmailVerificationTokenService;
import com.source.bundleboard.password.dto.PasswordConfirmResetInput;
import com.source.bundleboard.password.dto.PasswordChangeInput;
import com.source.bundleboard.password.dto.PasswordResetInput;
import com.source.bundleboard.password.dto.PasswordChangeResponse;
import com.source.bundleboard.password.dto.PasswordResetResponse;
import com.source.bundleboard.password.model.PasswordResetToken;
import com.source.bundleboard.password.model.PasswordResetType;
import com.source.bundleboard.password.properties.PasswordResetTokenProperties;
import com.source.bundleboard.password.repository.PasswordResetTokenRepository;
import com.source.bundleboard.rabbitmq.dto.EmailTask;
import com.source.bundleboard.rabbitmq.producer.TaskProducer;
import com.source.bundleboard.user.service.UserService;
import com.source.bundleboard.utils.AppLinkBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final EmailVerificationTokenService tokenService;

    private final PasswordResetTokenProperties passwordResetTokenProperties;

    private final TaskProducer taskProducer;

    private final MailProperties mailProperties;

    private final AppLinkBuilder appLinkBuilder;

    @Override
    public Mono<PasswordChangeResponse> requestPasswordChange(PasswordChangeInput input, UserDetails userDetails) {
        return userService.getUserByUsername(userDetails.getUsername())
                .flatMap(user -> {
                    if(!passwordEncoder.matches(input.currentPassword(), user.getPasswordHash())) {
                        return Mono.error(new UnmatchedPasswordsException());
                    }

                    String rawCode = generateCode();
                    PasswordResetToken token = createBaseToken(user.getId(), rawCode, input.newPassword());
                    token.setType(PasswordResetType.change_password);

                    return passwordResetTokenRepository.save(token)
                            .flatMap(savedToken -> {
                                EmailTask task = new EmailTask(
                                        user.getEmail(),
                                        mailProperties.getSubjects().getResetPassword(),
                                        mailProperties.getTemplates().getResetPassword(),
                                        Map.of("resetCode", rawCode)
                                );

                                return taskProducer.sendEmailTask(task);
                            })
                            .thenReturn(new PasswordChangeResponse(true, "Code sent successfully", null));

                });
    }

    @Override
    @Transactional
    public Mono<PasswordChangeResponse> confirmPasswordChange(String code, UserDetails userDetails) {
        String hashedCode = tokenService.sha256Hex(code);
        return userService.getUserByUsername(userDetails.getUsername())
                .flatMap(user -> passwordResetTokenRepository.findByUserIdAndCode(user.getId(), hashedCode)
                        .flatMap(token ->{
                            if (token.getBlockedUntil() != null && token.getBlockedUntil().isAfter(Instant.now())){
                                return Mono.error(new UserStatusException());
                            }

                            if (token.getExpiresAt().isBefore(Instant.now())){
                                return handleFailedAttempt(token);
                            }

                            user.setPasswordHash(token.getNewPasswordHash());
                            return userService.save(user)
                                    .thenReturn(new PasswordChangeResponse(true, "Password changed successfully", null));
                        })
                        .switchIfEmpty(Mono.error(new InvalidTokenException()))
                );
    }

    @Override
    public Mono<PasswordResetResponse> requestPasswordReset(PasswordResetInput input) {
        return userService.getUserByEmail(input.email())
                .flatMap(user -> {
                    String rawToken = tokenService.generateRawToken();
                    PasswordResetToken token = createBaseToken(user.getId(), rawToken, "");
                    token.setType(PasswordResetType.reset_password);

                    return passwordResetTokenRepository.save(token)
                            .flatMap(savedToken -> {
                                String resetLink = appLinkBuilder.buildLink(mailProperties.getPaths().getResetPassword(), rawToken);

                                EmailTask task = new EmailTask(
                                        user.getEmail(),
                                        mailProperties.getSubjects().getResetPassword(),
                                        mailProperties.getTemplates().getResetPassword(),
                                        Map.of("resetLink", resetLink)
                                );

                                return taskProducer.sendEmailTask(task);
                            })
                            .thenReturn(new PasswordResetResponse(true, "Reset link sent to email", rawToken));
                })
                .switchIfEmpty(Mono.error(new InvalidTokenException()));
    }

    @Override
    @Transactional
    public Mono<PasswordResetResponse> confirmPasswordReset(PasswordConfirmResetInput input) {
        String hashedToken = tokenService.sha256Hex(input.token());

        return passwordResetTokenRepository.findByCode(hashedToken)
                .filter(token -> token.getType() == PasswordResetType.reset_password)
                .flatMap(token -> {
                    if (token.getBlockedUntil() != null && token.getBlockedUntil().isAfter(Instant.now())) {
                        return Mono.error(new UserStatusException());
                    }

                    if (token.getExpiresAt().isBefore(Instant.now())) {
                        return handleResetFailedAttempt(token);
                    }

                    return userService.getUserById(token.getUserId())
                            .flatMap(user -> {
                                user.setPasswordHash(passwordEncoder.encode(input.newPassword()));
                                return userService.save(user)
                                        .thenReturn(new PasswordResetResponse(true, "Password reset successfully", null));
                            });
                })
                .switchIfEmpty(Mono.error(new InvalidTokenException()));
    }

    private PasswordResetToken createBaseToken(Long userId, String code, String newPassword) {
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setUserId(userId);
        passwordResetToken.setCode(tokenService.sha256Hex(code));
        passwordResetToken.setNewPasswordHash(passwordEncoder.encode(newPassword));
        passwordResetToken.setResendCount(0);
        passwordResetToken.setAttemptCount(0);
        passwordResetToken.setCreatedAt(java.time.Instant.now());
        passwordResetToken.setExpiresAt(java.time.Instant.now().plusSeconds(passwordResetTokenProperties.getBlockDurationSeconds()));
        return passwordResetToken;
    }

    private String generateCode() {
        return String.format("%06d", ThreadLocalRandom.current().nextInt(1000000));
    }

    private Mono<PasswordChangeResponse> handleFailedAttempt(PasswordResetToken token) {
        token.setAttemptCount(token.getAttemptCount() + 1);
        int left = passwordResetTokenProperties.getMaxAttempts() - token.getAttemptCount();

        if(left <= 0){
            token.setBlockedUntil(java.time.Instant.now().plusSeconds(passwordResetTokenProperties.getBlockDurationSeconds()));
        }
        return passwordResetTokenRepository.save(token)
                .flatMap(savedToken -> Mono.error(new InvalidTokenException()));

    }

    private Mono<PasswordResetResponse> handleResetFailedAttempt(PasswordResetToken token) {
        token.setAttemptCount(token.getAttemptCount() + 1);
        int left = Math.max(0, passwordResetTokenProperties.getMaxAttempts() - token.getAttemptCount());

        if (left <= 0) {
            token.setBlockedUntil(Instant.now().plusSeconds(passwordResetTokenProperties.getBlockDurationSeconds()));
        }

        return passwordResetTokenRepository.save(token)
                .flatMap(saved -> Mono.error(new InvalidTokenException()));
    }
}
