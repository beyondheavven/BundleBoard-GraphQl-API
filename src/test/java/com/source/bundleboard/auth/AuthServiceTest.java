package com.source.bundleboard.auth;

import com.source.bundleboard.api.exception.IncorrectPasswordException;
import com.source.bundleboard.api.exception.InvalidTokenException;
import com.source.bundleboard.api.exception.UserNotFoundException;
import com.source.bundleboard.auth.dto.AuthRequest;
import com.source.bundleboard.auth.dto.RefreshTokenRequest;
import com.source.bundleboard.auth.dto.RegisterRequest;
import com.source.bundleboard.auth.dto.SocialLoginRequest;
import com.source.bundleboard.auth.jwt.JwtProperties;
import com.source.bundleboard.auth.jwt.service.JwtService;
import com.source.bundleboard.auth.service.AuthServiceImpl;
import com.source.bundleboard.email.service.EmailVerificationTokenService;
import com.source.bundleboard.refreshtoken.model.RefreshToken;
import com.source.bundleboard.refreshtoken.repository.RefreshTokenRepository;
import com.source.bundleboard.user.model.User;
import com.source.bundleboard.user.model.UserRole;
import com.source.bundleboard.user.model.UserStatus;
import com.source.bundleboard.user.service.UserService;
import com.source.bundleboard.utils.PasswordGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private EmailVerificationTokenService emailVerificationTokenService;

    @Mock
    private PasswordGenerator passwordGenerator;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User(1L, "testuser", "test@test.com", "encodedPass",
                "", Set.of(UserRole.client), UserStatus.active,
                Instant.now(), Instant.now(), true);
    }

    @Test
    void authenticate_Success() {
        when(userService.findByUsername("testuser")).thenReturn(Mono.just(testUser));
        when(passwordEncoder.matches("rawPass", "encodedPass")).thenReturn(true);
        when(userService.save(any(User.class))).thenReturn(Mono.just(testUser));
        when(jwtService.generateAccessToken(any(), any(), any())).thenReturn("access");
        when(jwtService.generateRefreshToken(any(), any())).thenReturn("refresh");
        when(refreshTokenRepository.save(any())).thenReturn(Mono.just(new RefreshToken(1L, 1L, "refresh", Instant.now(), Instant.now())));

        StepVerifier.create(authService.authenticate(new AuthRequest("testuser", "rawPass")))
                .expectNextMatches(res -> res.accessToken().equals("access"))
                .verifyComplete();
    }

    @Test
    void authenticate_UserNotFound() {
        when(userService.findByUsername("unknown")).thenReturn(Mono.empty());
        StepVerifier.create(authService.authenticate(new AuthRequest("unknown", "pass")))
                .expectError(UserNotFoundException.class)
                .verify();
    }

    @Test
    void authenticate_IncorrectPassword() {
        when(userService.findByUsername("testuser")).thenReturn(Mono.just(testUser));
        when(passwordEncoder.matches("wrong", "encodedPass")).thenReturn(false);

        StepVerifier.create(authService.authenticate(new AuthRequest("testuser", "wrong")))
                .expectError(IncorrectPasswordException.class)
                .verify();
    }

    @Test
    void register_Success() {
        RegisterRequest req = new RegisterRequest("newuser", "email@test.com", "pass", UserRole.client);
        when(userService.existsByUsername("newuser")).thenReturn(Mono.just(false));
        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        when(userService.save(any(User.class))).thenReturn(Mono.just(testUser));
        when(emailVerificationTokenService.resendVerificationEmail(anyString())).thenReturn(Mono.empty());
        when(jwtService.generateAccessToken(any(), any(), any())).thenReturn("a");
        when(jwtService.generateRefreshToken(any(),any())).thenReturn("r");
        when(refreshTokenRepository.save(any())).thenReturn(Mono.just(new RefreshToken(1L, 1L, "r", Instant.now(), Instant.now())));

        StepVerifier.create(authService.register(req))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void refreshToken_Success() {
        String token = "validToken";
        when(jwtService.isRefreshToken(token)).thenReturn(Mono.just(true));
        when(refreshTokenRepository.existsByTokenAndExpirationTimeAfter(anyString(), any())).thenReturn(Mono.just(true));
        when(jwtService.extractUsername(token)).thenReturn(Mono.just("testuser"));
        when(userService.findByUsername("testuser")).thenReturn(Mono.just(testUser));
        when(refreshTokenRepository.deleteByToken(token)).thenReturn(Mono.empty());

        when(jwtService.generateAccessToken(any(),any(), any())).thenReturn("a");
        when(jwtService.generateRefreshToken(any(),any())).thenReturn("r");
        when(jwtProperties.getRefreshTokenExpirationMs()).thenReturn(1000L);
        when(refreshTokenRepository.save(any())).thenReturn(Mono.just(new RefreshToken(1L, 1L, "r", Instant.now(), Instant.now())));

        StepVerifier.create(authService.refreshToken(new RefreshTokenRequest(token)))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void refreshToken_InvalidToken() {
        when(jwtService.isRefreshToken("badToken")).thenReturn(Mono.just(false));

        StepVerifier.create(authService.refreshToken(new RefreshTokenRequest("badToken")))
                .expectError(InvalidTokenException.class)
                .verify();
    }

    @Test
    void logout_Success() {
        when(refreshTokenRepository.deleteByToken("token")).thenReturn(Mono.empty());

        StepVerifier.create(authService.logout(new RefreshTokenRequest("token")))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void socialLogin_Success_CreateNewUser() {
        SocialLoginRequest req = new SocialLoginRequest("social@test.com", "socialUser", "google");
        when(userService.getUserByEmail("social@test.com")).thenReturn(Mono.error(new UserNotFoundException()));
        when(passwordGenerator.generateSocialPassword()).thenReturn("randomPass");
        when(passwordEncoder.encode("randomPass")).thenReturn("encoded");
        when(userService.save(any(User.class))).thenReturn(Mono.just(testUser));
        when(jwtService.generateAccessToken(any(), any(), any())).thenReturn("a");
        when(jwtService.generateRefreshToken(any(), any())).thenReturn("r");
        when(jwtProperties.getRefreshTokenExpirationMs()).thenReturn(1000L);
        when(refreshTokenRepository.save(any())).thenReturn(Mono.just(new RefreshToken(1L, 1L, "r", Instant.now(), Instant.now())));

        StepVerifier.create(authService.socialLogin(req))
                .expectNextCount(1)
                .verifyComplete();
    }
}
