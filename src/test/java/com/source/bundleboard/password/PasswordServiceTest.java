package com.source.bundleboard.password;

import com.source.bundleboard.api.exception.InvalidTokenException;
import com.source.bundleboard.api.exception.UnmatchedPasswordsException;
import com.source.bundleboard.api.exception.UserStatusException;
import com.source.bundleboard.email.mail.propeties.MailProperties;
import com.source.bundleboard.email.properties.EmailVerificationProperties;
import com.source.bundleboard.email.service.EmailVerificationTokenService;
import com.source.bundleboard.password.dto.*;
import com.source.bundleboard.password.model.PasswordResetToken;
import com.source.bundleboard.password.model.PasswordResetType;
import com.source.bundleboard.password.properties.PasswordResetTokenProperties;
import com.source.bundleboard.password.repository.PasswordResetTokenRepository;
import com.source.bundleboard.password.service.PasswordServiceImpl;
import com.source.bundleboard.rabbitmq.dto.EmailTask;
import com.source.bundleboard.rabbitmq.producer.TaskProducer;
import com.source.bundleboard.user.model.User;
import com.source.bundleboard.user.service.UserService;
import com.source.bundleboard.utils.AppLinkBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PasswordServiceTest {

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private UserService userService;

    @Mock
    private TaskProducer taskProducer;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private MailProperties mailProperties;

    @Mock
    private EmailVerificationProperties emailVerificationProperties;

    @Mock
    private AppLinkBuilder appLinkBuilder;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailVerificationTokenService tokenService;

    @Mock
    private PasswordResetTokenProperties passwordResetTokenProperties;

    @InjectMocks
    private PasswordServiceImpl passwordService;

    private User mockUser;

    @Mock
    private UserDetails mockUserDetails;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("user@example.com");
        mockUser.setPasswordHash("current_hashed_password");

        lenient().when(mockUserDetails.getUsername()).thenReturn("user@example.com");
        lenient().when(passwordResetTokenProperties.getBlockDurationSeconds()).thenReturn(300);
        lenient().when(passwordResetTokenProperties.getMaxAttempts()).thenReturn(3);
        lenient().when(mailProperties.getSubjects().getResetPassword()).thenReturn("Reset Your Password");
        lenient().when(mailProperties.getTemplates().getResetPassword()).thenReturn("reset-password-template");
        lenient().when(mailProperties.getPaths().getResetPassword()).thenReturn("/reset-password");
    }

    @Nested
    class RequestPasswordChange {
        @Test
        void success() {
            PasswordChangeInput input = new PasswordChangeInput("oldPass", "newPass", "newPass");

            when(userService.getUserByUsername("user@example.com")).thenReturn(Mono.just(mockUser));
            when(passwordEncoder.matches("oldPass", "current_hashed_password")).thenReturn(true);
            when(tokenService.sha256Hex(anyString())).thenReturn("hashed_code");
            when(passwordEncoder.encode("newPass")).thenReturn("new_hashed_password");
            when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenReturn(Mono.just(new PasswordResetToken()));
            when(taskProducer.sendEmailTask(any(EmailTask.class))).thenReturn(Mono.empty());

            Mono<PasswordChangeResponse> result = passwordService.requestPasswordChange(input, mockUserDetails);

            StepVerifier.create(result)
                    .assertNext(response -> {
                        assertTrue(response.success());
                        assertEquals("Code sent successfully", response.message());
                    })
                    .verifyComplete();
        }

        @Test
        void unmatchedOldPassword_ThrowsException() {
            PasswordChangeInput input = new PasswordChangeInput("wrongOldPass", "newPass", "newPass");

            when(userService.getUserByUsername("user@example.com")).thenReturn(Mono.just(mockUser));
            when(passwordEncoder.matches("wrongOldPass", "current_hashed_password")).thenReturn(false);

            Mono<PasswordChangeResponse> result = passwordService.requestPasswordChange(input, mockUserDetails);

            StepVerifier.create(result)
                    .expectError(UnmatchedPasswordsException.class)
                    .verify();
        }
    }

    @Nested
    class ConfirmPasswordChange {
        @Test
        void success() {
            PasswordResetToken validToken = new PasswordResetToken();
            validToken.setNewPasswordHash("new_hashed_password");
            validToken.setExpiresAt(Instant.now().plus(1, ChronoUnit.HOURS));

            when(tokenService.sha256Hex("123456")).thenReturn("hashed_code");
            when(userService.getUserByUsername("user@example.com")).thenReturn(Mono.just(mockUser));
            when(passwordResetTokenRepository.findByUserIdAndCode(1L, "hashed_code")).thenReturn(Mono.just(validToken));
            when(userService.save(mockUser)).thenReturn(Mono.just(mockUser));

            Mono<PasswordChangeResponse> result = passwordService.confirmPasswordChange("123456", mockUserDetails);

            StepVerifier.create(result)
                    .assertNext(response -> {
                        assertTrue(response.success());
                        assertEquals("Password changed successfully", response.message());
                        assertEquals("new_hashed_password", mockUser.getPasswordHash());
                    })
                    .verifyComplete();
        }

        @Test
        void tokenBlocked_ThrowsUserStatusException() {
            PasswordResetToken blockedToken = new PasswordResetToken();
            blockedToken.setBlockedUntil(Instant.now().plus(10, ChronoUnit.MINUTES));

            when(tokenService.sha256Hex("123456")).thenReturn("hashed_code");
            when(userService.getUserByUsername("user@example.com")).thenReturn(Mono.just(mockUser));
            when(passwordResetTokenRepository.findByUserIdAndCode(1L, "hashed_code")).thenReturn(Mono.just(blockedToken));

            Mono<PasswordChangeResponse> result = passwordService.confirmPasswordChange("123456", mockUserDetails);

            StepVerifier.create(result)
                    .expectError(UserStatusException.class)
                    .verify();
        }

        @Test
        void tokenExpired_IncrementsAttemptAndThrowsInvalidTokenException() {
            PasswordResetToken expiredToken = new PasswordResetToken();
            expiredToken.setAttemptCount(0);
            expiredToken.setExpiresAt(Instant.now().minus(10, ChronoUnit.MINUTES));

            when(tokenService.sha256Hex("123456")).thenReturn("hashed_code");
            when(userService.getUserByUsername("user@example.com")).thenReturn(Mono.just(mockUser));
            when(passwordResetTokenRepository.findByUserIdAndCode(1L, "hashed_code")).thenReturn(Mono.just(expiredToken));
            when(passwordResetTokenRepository.save(expiredToken)).thenReturn(Mono.just(expiredToken));

            Mono<PasswordChangeResponse> result = passwordService.confirmPasswordChange("123456", mockUserDetails);

            StepVerifier.create(result)
                    .expectError(InvalidTokenException.class)
                    .verify();

            assertEquals(1, expiredToken.getAttemptCount());
        }
    }

    @Nested
    class RequestPasswordReset {
        @Test
        void success() {
            PasswordResetInput input = new PasswordResetInput("user@example.com");

            when(userService.getUserByEmail("user@example.com")).thenReturn(Mono.just(mockUser));
            when(tokenService.generateRawToken()).thenReturn("raw_reset_token");
            when(tokenService.sha256Hex("raw_reset_token")).thenReturn("hashed_reset_token");
            when(passwordEncoder.encode("")).thenReturn("empty_hash");
            when(passwordResetTokenRepository.save(any(PasswordResetToken.class)))
                    .thenReturn(Mono.just(new PasswordResetToken()));
            when(appLinkBuilder.buildLink(any(), eq("raw_reset_token"))).thenReturn("http://mock-link");
            when(taskProducer.sendEmailTask(any(EmailTask.class))).thenReturn(Mono.empty());

            Mono<PasswordResetResponse> result = passwordService.requestPasswordReset(input);

            StepVerifier.create(result)
                    .assertNext(response -> {
                        assertTrue(response.success());
                        assertEquals("raw_reset_token", response.token());
                    })
                    .verifyComplete();
        }

        @Test
        void userNotFound_ThrowsInvalidTokenException() {
            PasswordResetInput input = new PasswordResetInput("missing@example.com");

            when(userService.getUserByEmail("missing@example.com")).thenReturn(Mono.empty());

            StepVerifier.create(passwordService.requestPasswordReset(input))
                    .expectError(InvalidTokenException.class)
                    .verify();
        }
    }

    @Nested
    class ConfirmPasswordReset {
        @Test
        void success() {
            PasswordConfirmResetInput input = new PasswordConfirmResetInput("raw_token", "newSecPass", "newSecPass");
            PasswordResetToken validToken = new PasswordResetToken();
            validToken.setUserId(1L);
            validToken.setType(PasswordResetType.reset_password);
            validToken.setExpiresAt(Instant.now().plus(1, ChronoUnit.HOURS));

            when(tokenService.sha256Hex("raw_token")).thenReturn("hashed_token");
            when(passwordResetTokenRepository.findByCode("hashed_token")).thenReturn(Mono.just(validToken));
            when(userService.getUserById(1L)).thenReturn(Mono.just(mockUser));
            when(passwordEncoder.encode("newSecPass")).thenReturn("new_secure_hash");
            when(userService.save(mockUser)).thenReturn(Mono.just(mockUser));

            Mono<PasswordResetResponse> result = passwordService.confirmPasswordReset(input);

            StepVerifier.create(result)
                    .assertNext(response -> {
                        assertTrue(response.success());
                        assertEquals("Password reset successfully", response.message());
                        assertEquals("new_secure_hash", mockUser.getPasswordHash());
                    })
                    .verifyComplete();
        }

        @Test
        void wrongTokenType_ReturnsEmptyAndThrowsInvalidTokenException() {
            PasswordConfirmResetInput input = new PasswordConfirmResetInput("raw_token", "newSecPass", "newSecPass");
            PasswordResetToken wrongTypeToken = new PasswordResetToken();
            wrongTypeToken.setType(PasswordResetType.change_password);

            when(tokenService.sha256Hex("raw_token")).thenReturn("hashed_token");
            when(passwordResetTokenRepository.findByCode("hashed_token")).thenReturn(Mono.just(wrongTypeToken));

            Mono<PasswordResetResponse> result = passwordService.confirmPasswordReset(input);

            StepVerifier.create(result)
                    .expectError(InvalidTokenException.class)
                    .verify();
        }

        @Test
        void tokenExpired_ThrowsInvalidTokenException() {
            PasswordConfirmResetInput input = new PasswordConfirmResetInput("raw_token", "newSecPass", "newSecPass");
            PasswordResetToken expiredToken = new PasswordResetToken();
            expiredToken.setType(PasswordResetType.reset_password);
            expiredToken.setExpiresAt(Instant.now().minus(1, ChronoUnit.HOURS));
            expiredToken.setAttemptCount(0);

            when(tokenService.sha256Hex("raw_token")).thenReturn("hashed_token");
            when(passwordResetTokenRepository.findByCode("hashed_token")).thenReturn(Mono.just(expiredToken));
            when(passwordResetTokenRepository.save(expiredToken)).thenReturn(Mono.just(expiredToken));

            StepVerifier.create(passwordService.confirmPasswordReset(input))
                    .expectError(InvalidTokenException.class)
                    .verify();

            assertEquals(1, expiredToken.getAttemptCount());
        }

        @Test
        void tokenBlocked_ThrowsUserStatusException() {
            PasswordConfirmResetInput input = new PasswordConfirmResetInput("raw_token", "newSecPass", "newSecPass");
            PasswordResetToken blockedToken = new PasswordResetToken();
            blockedToken.setType(PasswordResetType.reset_password);
            blockedToken.setBlockedUntil(Instant.now().plus(1, ChronoUnit.HOURS));

            when(tokenService.sha256Hex("raw_token")).thenReturn("hashed_token");
            when(passwordResetTokenRepository.findByCode("hashed_token")).thenReturn(Mono.just(blockedToken));

            StepVerifier.create(passwordService.confirmPasswordReset(input))
                    .expectError(UserStatusException.class)
                    .verify();
        }
    }
}