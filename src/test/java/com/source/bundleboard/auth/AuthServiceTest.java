package com.source.bundleboard.auth;

import com.source.bundleboard.auth.dto.AuthRequest;
import com.source.bundleboard.auth.jwt.JwtProperties;
import com.source.bundleboard.auth.jwt.service.JwtService;
import com.source.bundleboard.auth.service.AuthServiceImpl;
import com.source.bundleboard.email.service.EmailVerificationTokenService;
import com.source.bundleboard.refreshtoken.model.RefreshToken;
import com.source.bundleboard.refreshtoken.repository.RefreshTokenRepository;
import com.source.bundleboard.user.model.User;
import com.source.bundleboard.user.model.UserStatus;
import com.source.bundleboard.user.service.UserService;
import com.source.bundleboard.utils.PasswordGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AuthServiceTest {

    @Mock
    private UserService userService;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private JwtService jwtService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtProperties jwtProperties;
    @Mock private EmailVerificationTokenService emailVerificationTokenService;
    @Mock private PasswordGenerator passwordGenerator;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void authenticate_Success() {
        // GIVEN
        AuthRequest request = new AuthRequest("user", "pass");

        // Используем 1L для Long ID
        User user = new User(1L, "user", "email", "hash", "",
                Collections.emptySet(), UserStatus.active,
                Instant.now(), Instant.now(), true);

        when(userService.findByUsername("user")).thenReturn(Mono.just(user));
        when(passwordEncoder.matches("pass", "hash")).thenReturn(true);
        when(userService.save(any(User.class))).thenReturn(Mono.just(user));

        when(jwtService.generateAccessToken(any(), any())).thenReturn("access");
        when(jwtService.generateRefreshToken(any())).thenReturn("refresh");
        when(jwtProperties.getRefreshTokenExpirationMs()).thenReturn(3600000L);

        // Важно: RefreshToken тоже может содержать ID типа Long
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenReturn(Mono.just(new RefreshToken(1L, 1L, "refresh", Instant.now(), Instant.now())));

        // WHEN & THEN
        StepVerifier.create(authService.authenticate(request))
                .expectNextMatches(response -> response.accessToken().equals("access")
                        && response.user().username().equals("user"))
                .verifyComplete();
    }
}
