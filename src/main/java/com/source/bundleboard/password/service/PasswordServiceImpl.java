package com.source.bundleboard.password.service;

import com.source.bundleboard.api.exception.InvalidTokenException;
import com.source.bundleboard.api.exception.UnmatchedPasswordsException;
import com.source.bundleboard.api.exception.UserStatusException;
import com.source.bundleboard.email.mail.service.MailService;
import com.source.bundleboard.email.service.EmailVerificationTokenService;
import com.source.bundleboard.password.dto.PasswordChangeResponse;
import com.source.bundleboard.password.dto.PasswordChangeInput;
import com.source.bundleboard.password.mapper.PasswordMapper;
import com.source.bundleboard.password.model.PasswordResetToken;
import com.source.bundleboard.password.model.PasswordResetType;
import com.source.bundleboard.password.properties.PasswordResetTokenProperties;
import com.source.bundleboard.password.repository.PasswordResetTokenRepository;
import com.source.bundleboard.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private final PasswordMapper passwordMapper;

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final UserService userService;

    private final MailService mailService;

    private final PasswordEncoder passwordEncoder;

    private final EmailVerificationTokenService tokenService;

    private final PasswordResetTokenProperties passwordResetTokenProperties;

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
                            .flatMap(savedToken -> mailService.sendPasswordResetEmail(user.getEmail(), rawCode))
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
                            return userService.saveUser(user)
                                    .thenReturn(new PasswordChangeResponse(true, "Password changed successfully", null));
                        })
                        .switchIfEmpty(Mono.error(new InvalidTokenException()))
                );
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
}
