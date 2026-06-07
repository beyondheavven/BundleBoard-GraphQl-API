package com.source.bundleboard.email;

import com.source.bundleboard.api.exception.InvalidEmailVerificationTokenException;
import com.source.bundleboard.api.exception.UserNotFoundException;
import com.source.bundleboard.api.exception.UserStatusException;
import com.source.bundleboard.email.model.EmailTokenStatus;
import com.source.bundleboard.email.model.EmailTokenType;
import com.source.bundleboard.email.model.EmailVerificationToken;
import com.source.bundleboard.email.properties.EmailVerificationProperties;
import com.source.bundleboard.email.repository.EmailVerificationTokenRepository;
import com.source.bundleboard.email.service.EmailVerificationTokenServiceImpl;
import com.source.bundleboard.rabbitmq.dto.EmailTask;
import com.source.bundleboard.rabbitmq.producer.TaskProducer;
import com.source.bundleboard.user.model.User;
import com.source.bundleboard.user.model.UserStatus;
import com.source.bundleboard.user.service.UserService;
import com.source.bundleboard.utils.AppLinkBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailVerificationTokenServiceTest {

    @Mock
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Mock
    private EmailVerificationProperties emailVerificationProperties;

    @Mock
    private UserService userService;

    @Mock
    private TaskProducer taskProducer;

    @Mock
    private AppLinkBuilder appLinkBuilder;

    @InjectMocks
    @Spy
    private EmailVerificationTokenServiceImpl emailTokenService;

    private EmailVerificationToken sampleToken;
    private User sampleUser;
    private final String rawToken = "raw-random-token-123";
    private final String hashedToken = "hashed-token-123";

    @BeforeEach
    void setUp() {
        lenient().when(emailVerificationProperties.getByteLength()).thenReturn(16);
        lenient().when(emailVerificationProperties.getHashAlgorithm()).thenReturn("SHA-256");
        lenient().when(emailVerificationProperties.getHashLength()).thenReturn(64);
        lenient().when(emailVerificationProperties.getHexFormat()).thenReturn("%02x");
        lenient().when(emailVerificationProperties.getMaxAttempts()).thenReturn(3);
        lenient().when(emailVerificationProperties.getBlockDurationSeconds()).thenReturn(600);

        sampleToken = new EmailVerificationToken();
        sampleToken.setId(1L);
        sampleToken.setUserId(42L);
        sampleToken.setToken(hashedToken);
        sampleToken.setEmailTokenStatus(EmailTokenStatus.pending);
        sampleToken.setEmailTokenType(EmailTokenType.verify_email);
        sampleToken.setCreatedAt(Instant.now().minusSeconds(60));
        sampleToken.setExpiresAt(Instant.now().plusSeconds(3600));
        sampleToken.setAttemptCount(0);

        sampleUser = new User();
        sampleUser.setId(42L);
        sampleUser.setUsername("john_doe");
        sampleUser.setEmail("john@example.com");
        sampleUser.setStatus(UserStatus.inactive);
    }


    @Test
    void verifyEmail_Success_VerifyEmailType() {
        doReturn(hashedToken).when(emailTokenService).sha256Hex(rawToken);

        when(emailVerificationTokenRepository.findByToken(hashedToken)).thenReturn(Mono.just(sampleToken));
        when(userService.getUserById(42L)).thenReturn(Mono.just(sampleUser));
        when(userService.save(any(User.class))).thenReturn(Mono.just(sampleUser));
        when(emailVerificationTokenRepository.save(any(EmailVerificationToken.class))).thenReturn(Mono.just(sampleToken));

        StepVerifier.create(emailTokenService.verifyEmail(rawToken))
                .assertNext(response -> {
                    assertTrue(response.success());
                    assertEquals("Email verified successfully", response.message());
                })
                .verifyComplete();

        assertEquals(UserStatus.active, sampleUser.getStatus());
        assertEquals(EmailTokenStatus.verified, sampleToken.getEmailTokenStatus());
    }

    @Test
    void verifyEmail_Success_ChangeEmailType() {
        doReturn(hashedToken).when(emailTokenService).sha256Hex(rawToken);

        sampleToken.setEmailTokenType(EmailTokenType.change_email);
        sampleToken.setNewEmail("new_john@example.com");

        when(emailVerificationTokenRepository.findByToken(hashedToken)).thenReturn(Mono.just(sampleToken));
        when(userService.getUserById(42L)).thenReturn(Mono.just(sampleUser));
        when(userService.save(any(User.class))).thenReturn(Mono.just(sampleUser));
        when(emailVerificationTokenRepository.save(any(EmailVerificationToken.class))).thenReturn(Mono.just(sampleToken));

        StepVerifier.create(emailTokenService.verifyEmail(rawToken))
                .assertNext(response -> assertTrue(response.success()))
                .verifyComplete();

        assertEquals("new_john@example.com", sampleUser.getEmail());
    }

    @Test
    void verifyEmail_TokenNotFound_ThrowsInvalidTokenException() {
        doReturn(hashedToken).when(emailTokenService).sha256Hex(rawToken);
        when(emailVerificationTokenRepository.findByToken(hashedToken)).thenReturn(Mono.empty());

        StepVerifier.create(emailTokenService.verifyEmail(rawToken))
                .expectError(InvalidEmailVerificationTokenException.class)
                .verify();
    }

    @Test
    void verifyEmail_TokenBlocked_ThrowsUserStatusException() {
        doReturn(hashedToken).when(emailTokenService).sha256Hex(rawToken);
        sampleToken.setBlockedUntil(Instant.now().plusSeconds(300));

        when(emailVerificationTokenRepository.findByToken(hashedToken)).thenReturn(Mono.just(sampleToken));

        StepVerifier.create(emailTokenService.verifyEmail(rawToken))
                .expectError(UserStatusException.class)
                .verify();

        verifyNoInteractions(userService);
    }

    @Test
    void verifyEmail_TokenExpired_IncrementsAttemptsAndThrowsInvalidTokenException() {
        doReturn(hashedToken).when(emailTokenService).sha256Hex(rawToken);
        sampleToken.setExpiresAt(Instant.now().minusSeconds(10));

        when(emailVerificationTokenRepository.findByToken(hashedToken)).thenReturn(Mono.just(sampleToken));
        when(emailVerificationTokenRepository.save(any(EmailVerificationToken.class))).thenReturn(Mono.just(sampleToken));

        StepVerifier.create(emailTokenService.verifyEmail(rawToken))
                .expectError(InvalidEmailVerificationTokenException.class)
                .verify();

        assertEquals(1, sampleToken.getAttemptCount());
        assertNull(sampleToken.getBlockedUntil());
    }

    @Test
    void verifyEmail_MaxAttemptsReached_BlocksToken() {
        doReturn(hashedToken).when(emailTokenService).sha256Hex(rawToken);
        sampleToken.setAttemptCount(2);
        sampleToken.setExpiresAt(Instant.now().minusSeconds(10));

        when(emailVerificationTokenRepository.findByToken(hashedToken)).thenReturn(Mono.just(sampleToken));
        when(emailVerificationTokenRepository.save(any(EmailVerificationToken.class))).thenReturn(Mono.just(sampleToken));

        StepVerifier.create(emailTokenService.verifyEmail(rawToken))
                .expectError(InvalidEmailVerificationTokenException.class)
                .verify();

        assertEquals(3, sampleToken.getAttemptCount());
        assertNotNull(sampleToken.getBlockedUntil());
    }


    @Test
    void sendChangeEmailToken_Success() {
        doReturn(rawToken).when(emailTokenService).generateRawToken();
        doReturn(hashedToken).when(emailTokenService).sha256Hex(rawToken);

        when(userService.getUserByUsername("john_doe")).thenReturn(Mono.just(sampleUser));
        when(emailVerificationTokenRepository.save(any(EmailVerificationToken.class))).thenReturn(Mono.just(sampleToken));
        when(appLinkBuilder.buildLink(anyString(), anyString())).thenReturn("http://link");
        when(taskProducer.sendEmailTask(any(EmailTask.class))).thenReturn(Mono.empty());

        StepVerifier.create(emailTokenService.sendChangeEmailToken("new@example.com", "john_doe"))
                .assertNext(response -> {
                    assertTrue(response.success());
                    assertEquals("Email verification link sent to new@example.com", response.message());
                })
                .verifyComplete();

        verify(taskProducer).sendEmailTask(any(EmailTask.class));
    }

    @Test
    void resendVerificationEmail_Success() {
        doReturn(rawToken).when(emailTokenService).generateRawToken();
        doReturn(hashedToken).when(emailTokenService).sha256Hex(rawToken);

        when(userService.getUserByEmail("john@example.com")).thenReturn(Mono.just(sampleUser));
        when(emailVerificationTokenRepository.save(any(EmailVerificationToken.class))).thenReturn(Mono.just(sampleToken));

        when(appLinkBuilder.buildLink(anyString(), anyString())).thenReturn("http://link");
        when(taskProducer.sendEmailTask(any(EmailTask.class))).thenReturn(Mono.empty());

        StepVerifier.create(emailTokenService.resendVerificationEmail("john@example.com"))
                .assertNext(response -> assertTrue(response.success()))
                .verifyComplete();

        verify(taskProducer).sendEmailTask(any(EmailTask.class));
    }

    @Test
    void resendVerificationEmail_UserNotFound_ThrowsException() {
        when(userService.getUserByEmail("missing@example.com")).thenReturn(Mono.empty());

        StepVerifier.create(emailTokenService.resendVerificationEmail("missing@example.com"))
                .expectError(UserNotFoundException.class)
                .verify();
        verifyNoInteractions(taskProducer, emailVerificationTokenRepository);
    }
}
