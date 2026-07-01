package com.source.bundleboard.auth;

import com.source.bundleboard.api.exception.*;
import com.source.bundleboard.auth.dto.*;
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
import io.jsonwebtoken.Claims;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.lenient;

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

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private RefreshToken mockRefreshToken;

    @BeforeEach
    void setUp() {
        testUser = new User(1L, "testuser", "test@test.com", "encodedPass",
                "", Set.of(UserRole.client), UserStatus.active,
                Instant.now(), Instant.now(), true);

        mockRefreshToken = new RefreshToken(1L, 1L, "new-refresh", Instant.now(), Instant.now().plusMillis(1000));
    }

    // --- AUTHENTICATE TESTS ---

    @Test
    void authenticate_Success_WithUsername() {
        // Изменено на findByIdentifier
        when(userService.findByIdentifier("testuser")).thenReturn(Mono.just(testUser));
        when(passwordEncoder.matches("rawPass", "encodedPass")).thenReturn(true);
        when(userService.save(any(User.class))).thenReturn(Mono.just(testUser));

        when(jwtService.generateAccessToken(any(), any(), any())).thenReturn("access");
        when(jwtService.generateRefreshToken(any(), any())).thenReturn("new-refresh");
        when(jwtProperties.getRefreshTokenExpirationMs()).thenReturn(86400000L);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(Mono.just(mockRefreshToken));

        // Конструктор AuthRequest теперь принимает универсальный идентифактор
        StepVerifier.create(authService.authenticate(new AuthRequest("testuser", "rawPass")))
                .expectNextMatches(res -> res.accessToken().equals("access") && res.refreshToken().equals("new-refresh"))
                .verifyComplete();
    }

    @Test
    void authenticate_Success_WithEmail() {
        // Добавлен тест для верификации входа по Email
        when(userService.findByIdentifier("test@test.com")).thenReturn(Mono.just(testUser));
        when(passwordEncoder.matches("rawPass", "encodedPass")).thenReturn(true);
        when(userService.save(any(User.class))).thenReturn(Mono.just(testUser));

        when(jwtService.generateAccessToken(any(), any(), any())).thenReturn("access");
        when(jwtService.generateRefreshToken(any(), any())).thenReturn("new-refresh");
        when(jwtProperties.getRefreshTokenExpirationMs()).thenReturn(86400000L);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(Mono.just(mockRefreshToken));

        StepVerifier.create(authService.authenticate(new AuthRequest("test@test.com", "rawPass")))
                .expectNextMatches(res -> res.accessToken().equals("access") && res.refreshToken().equals("new-refresh"))
                .verifyComplete();
    }

    @Test
    void authenticate_UserNotFound() {
        // Изменено на findByIdentifier
        when(userService.findByIdentifier("unknown")).thenReturn(Mono.empty());

        StepVerifier.create(authService.authenticate(new AuthRequest("unknown", "pass")))
                .expectError(UserNotFoundException.class)
                .verify();
    }

    @Test
    void authenticate_IncorrectPassword() {
        // Изменено на findByIdentifier
        when(userService.findByIdentifier("testuser")).thenReturn(Mono.just(testUser));
        when(passwordEncoder.matches("wrong", "encodedPass")).thenReturn(false);

        StepVerifier.create(authService.authenticate(new AuthRequest("testuser", "wrong")))
                .expectError(IncorrectPasswordException.class)
                .verify();
    }

    @Test
    void authenticate_BannedUser_ThrowsUserStatusException() {
        User bannedUser = new User(1L, "testuser", "test@test.com", "encodedPass",
                "", Set.of(UserRole.client), UserStatus.banned, null, null, true);

        // Изменено на findByIdentifier
        when(userService.findByIdentifier("testuser")).thenReturn(Mono.just(bannedUser));
        when(passwordEncoder.matches("rawPass", "encodedPass")).thenReturn(true);

        StepVerifier.create(authService.authenticate(new AuthRequest("testuser", "rawPass")))
                .expectError(UserStatusException.class)
                .verify();
    }

    // --- REGISTER TESTS ---

    @Test
    void register_Success() {
        RegisterRequest req = new RegisterRequest("newuser", "email@test.com", "pass", UserRole.client);

        when(userService.existsByUsername("newuser")).thenReturn(Mono.just(false));
        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        when(userService.save(any(User.class))).thenReturn(Mono.just(testUser));
        when(emailVerificationTokenService.resendVerificationEmail(anyString())).thenReturn(Mono.empty());

        when(jwtService.generateAccessToken(any(), any(), any())).thenReturn("access");
        when(jwtService.generateRefreshToken(any(), any())).thenReturn("new-refresh");
        when(jwtProperties.getRefreshTokenExpirationMs()).thenReturn(86400000L);
        when(refreshTokenRepository.save(any())).thenReturn(Mono.just(mockRefreshToken));

        StepVerifier.create(authService.register(req))
                .expectNextMatches(res -> res.accessToken().equals("access"))
                .verifyComplete();
    }

    @Test
    void register_UserAlreadyExists() {
        RegisterRequest req = new RegisterRequest("existinguser", "email@test.com", "pass", UserRole.client);
        when(userService.existsByUsername("existinguser")).thenReturn(Mono.just(true));

        StepVerifier.create(authService.register(req))
                .expectNextMatches(res -> res.error() != null && res.error().contains("already exists"))
                .verifyComplete();
    }

    // --- REFRESH TOKEN TESTS ---

    @Test
    void refreshToken_Success() {
        String oldToken = "validToken";
        Claims mockClaims = mock(Claims.class);

        when(jwtService.isRefreshToken(oldToken)).thenReturn(Mono.just(true));
        when(refreshTokenRepository.existsByTokenAndExpirationTimeAfter(anyString(), any())).thenReturn(Mono.just(true));
        when(jwtService.validateToken(oldToken)).thenReturn(Mono.just(mockClaims));
        withMockUserId(mockClaims, 1L);
        when(userService.getUserById(1L)).thenReturn(Mono.just(testUser));
        when(refreshTokenRepository.deleteByToken(oldToken)).thenReturn(Mono.empty());

        when(jwtService.generateAccessToken(any(), any(), any())).thenReturn("access");
        when(jwtService.generateRefreshToken(any(), any())).thenReturn("new-refresh");
        when(jwtProperties.getRefreshTokenExpirationMs()).thenReturn(86400000L);
        when(refreshTokenRepository.save(any())).thenReturn(Mono.just(mockRefreshToken));

        StepVerifier.create(authService.refreshToken(new RefreshTokenRequest(oldToken)))
                .expectNextMatches(res -> res.accessToken().equals("access") && res.refreshToken().equals("new-refresh"))
                .verifyComplete();
    }

    @Test
    void refreshToken_LegacyFallback_Success() {
        String oldToken = "legacyToken";
        Claims mockClaims = mock(Claims.class);

        when(jwtService.isRefreshToken(oldToken)).thenReturn(Mono.just(true));
        when(refreshTokenRepository.existsByTokenAndExpirationTimeAfter(anyString(), any())).thenReturn(Mono.just(true));
        when(jwtService.validateToken(oldToken)).thenReturn(Mono.just(mockClaims));
        withMockUserId(mockClaims, null);
        when(mockClaims.getSubject()).thenReturn("testuser");
        when(userService.getUserByUsername("testuser")).thenReturn(Mono.just(testUser));
        when(refreshTokenRepository.deleteByToken(oldToken)).thenReturn(Mono.empty());

        when(jwtService.generateAccessToken(any(), any(), any())).thenReturn("access");
        when(jwtService.generateRefreshToken(any(), any())).thenReturn("new-refresh");
        when(jwtProperties.getRefreshTokenExpirationMs()).thenReturn(86400000L);
        when(refreshTokenRepository.save(any())).thenReturn(Mono.just(mockRefreshToken));

        StepVerifier.create(authService.refreshToken(new RefreshTokenRequest(oldToken)))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void refreshToken_InvalidTokenFormat() {
        when(jwtService.isRefreshToken("badToken")).thenReturn(Mono.just(false));

        StepVerifier.create(authService.refreshToken(new RefreshTokenRequest("badToken")))
                .expectError(InvalidTokenException.class)
                .verify();
    }

    // --- LOGOUT TESTS ---

    @Test
    void logout_Success() {
        when(refreshTokenRepository.deleteByToken("token")).thenReturn(Mono.empty());

        StepVerifier.create(authService.logout(new RefreshTokenRequest("token")))
                .expectNext(true)
                .verifyComplete();
    }

    // --- SOCIAL LOGIN TESTS ---

    @Test
    void socialLogin_Success() {
        SocialLoginRequest req = new SocialLoginRequest("test@test.com", "testuser", "google");

        when(userService.getUserByEmail("test@test.com")).thenReturn(Mono.just(testUser));
        when(userService.save(any(User.class))).thenReturn(Mono.just(testUser));

        when(jwtService.generateAccessToken(any(), any(), any())).thenReturn("access");
        when(jwtService.generateRefreshToken(any(), any())).thenReturn("new-refresh");
        when(jwtProperties.getRefreshTokenExpirationMs()).thenReturn(86400000L);
        when(refreshTokenRepository.save(any())).thenReturn(Mono.just(mockRefreshToken));

        StepVerifier.create(authService.socialLogin(req))
                .expectNextMatches(res -> res.accessToken().equals("access"))
                .verifyComplete();
    }

    @Test
    void socialLogin_UserNotFound() {
        SocialLoginRequest req = new SocialLoginRequest("unknown@test.com", "unknown", "google");
        when(userService.getUserByEmail("unknown@test.com")).thenReturn(Mono.empty());

        StepVerifier.create(authService.socialLogin(req))
                .expectError(UserNotFoundException.class)
                .verify();
    }

    // --- SOCIAL REGISTER TESTS ---

    @Test
    void socialRegister_Success() {
        SocialRegisterRequest req = new SocialRegisterRequest("socialUser", "social@test.com", "pass", UserRole.client);

        when(userService.getUserByEmail("social@test.com")).thenReturn(Mono.error(new UserNotFoundException()));
        when(userService.existsByUsername("socialUser")).thenReturn(Mono.just(false));
        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        when(userService.save(any(User.class))).thenReturn(Mono.just(testUser));

        when(jwtService.generateAccessToken(any(), any(), any())).thenReturn("access");
        when(jwtService.generateRefreshToken(any(), any())).thenReturn("new-refresh");
        when(jwtProperties.getRefreshTokenExpirationMs()).thenReturn(86400000L);
        when(refreshTokenRepository.save(any())).thenReturn(Mono.just(mockRefreshToken));

        StepVerifier.create(authService.socialRegister(req))
                .expectNextMatches(res -> res.accessToken().equals("access"))
                .verifyComplete();
    }

    @Test
    void socialRegister_EmailAlreadyExists() {
        SocialRegisterRequest req = new SocialRegisterRequest("socialUser", "existing@test.com", "pass", UserRole.client);

        when(userService.getUserByEmail("existing@test.com")).thenReturn(Mono.just(testUser));
        lenient().when(userService.existsByUsername(anyString())).thenReturn(Mono.just(false));
        StepVerifier.create(authService.socialRegister(req))
                .expectError(UserAlreadyExistsException.class)
                .verify();
    }

    private void withMockUserId(Claims mockClaims, Long userId) {
        when(mockClaims.get("userId", Number.class)).thenReturn(userId);
    }
}