package com.source.bundleboard.jwt;

import com.source.bundleboard.auth.jwt.JwtProperties;
import com.source.bundleboard.auth.jwt.service.JwtServiceImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.test.StepVerifier;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private JwtProperties jwtProperties;

    private JwtServiceImpl jwtService;
    private final String SECRET_KEY = "my-super-secret-test-key-must-be-at-least-32-bytes";
    private final long ACCESS_EXPIRATION = 900000L;
    private final long REFRESH_EXPIRATION = 2592000000L;

    @BeforeEach
    void setUp() {
        when(jwtProperties.getSecret()).thenReturn(SECRET_KEY);

        jwtService = new JwtServiceImpl(jwtProperties);
    }

    @Test
    void generateAccessToken_ShouldReturnValidToken() {
        when(jwtProperties.getAccessTokenExpirationMs()).thenReturn(ACCESS_EXPIRATION);
        Long userId = 1L;
        String username = "testuser";
        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        String token = jwtService.generateAccessToken(userId, username, authorities);

        assertThat(token).isNotBlank();

        StepVerifier.create(jwtService.validateToken(token))
                .assertNext(claims -> {
                    assertThat(claims.getSubject()).isEqualTo(username);
                    assertThat(claims.get("userId", Long.class)).isEqualTo(userId);
                    assertThat(claims.get("type", String.class)).isEqualTo("access");

                    List<String> roles = claims.get("roles", List.class);
                    assertThat(roles).containsExactly("ROLE_USER");
                })
                .verifyComplete();
    }

    @Test
    void generateRefreshToken_ShouldReturnValidToken() {
        when(jwtProperties.getRefreshTokenExpirationMs()).thenReturn(REFRESH_EXPIRATION);
        Long userId = 2L;
        String username = "refreshuser";

        String token = jwtService.generateRefreshToken(userId, username);

        assertThat(token).isNotBlank();

        StepVerifier.create(jwtService.validateToken(token))
                .assertNext(claims -> {
                    assertThat(claims.getSubject()).isEqualTo(username);
                    assertThat(claims.get("userId", Long.class)).isEqualTo(userId);
                    assertThat(claims.get("type", String.class)).isEqualTo("refresh");
                    assertThat(claims.get("roles")).isNull(); // У рефреш-токена нет ролей
                })
                .verifyComplete();
    }

    @Test
    void validateToken_ShouldReturnEmpty_WhenTokenIsExpired() {
        when(jwtProperties.getAccessTokenExpirationMs()).thenReturn(0L);
        String expiredToken = jwtService.generateAccessToken(1L, "user", List.of());
        StepVerifier.create(jwtService.validateToken(expiredToken))
                .verifyComplete();
    }

    @Test
    void validateToken_ShouldReturnEmpty_WhenTokenIsMalformedOrInvalidSignature() {
        String invalidToken = "ey.invalid.token.structure";

        StepVerifier.create(jwtService.validateToken(invalidToken))
                .verifyComplete();
    }

    @Test
    void validateToken_ShouldReturnEmpty_WhenSignedWithDifferentKey() {
        String wrongKey = "another-secret-key-that-is-also-32-bytes-long";
        String tokenSignedWithWrongKey = Jwts.builder()
                .subject("hacker")
                .expiration(new Date(System.currentTimeMillis() + 100000))
                .signWith(Keys.hmacShaKeyFor(wrongKey.getBytes(StandardCharsets.UTF_8)))
                .compact();

        StepVerifier.create(jwtService.validateToken(tokenSignedWithWrongKey))
                .verifyComplete();
    }

    @Test
    void extractUsername_ShouldReturnUsername_WhenTokenIsValid() {
        when(jwtProperties.getAccessTokenExpirationMs()).thenReturn(ACCESS_EXPIRATION);
        String token = jwtService.generateAccessToken(1L, "johndoe", List.of());

        StepVerifier.create(jwtService.extractUsername(token))
                .expectNext("johndoe")
                .verifyComplete();
    }

    @Test
    void extractAuthorities_ShouldReturnRoles_WhenRolesExist() {
        when(jwtProperties.getAccessTokenExpirationMs()).thenReturn(ACCESS_EXPIRATION);
        Collection<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );
        String token = jwtService.generateAccessToken(1L, "admin", authorities);

        StepVerifier.create(jwtService.extractAuthorities(token))
                .assertNext(extractedAuthorities -> {
                    assertThat(extractedAuthorities).hasSize(2);
                    assertThat(extractedAuthorities.stream().map(GrantedAuthority::getAuthority))
                            .containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
                })
                .verifyComplete();
    }

    @Test
    void extractAuthorities_ShouldReturnEmptyList_WhenNoRolesExist() {
        when(jwtProperties.getRefreshTokenExpirationMs()).thenReturn(REFRESH_EXPIRATION);
        String refreshToken = jwtService.generateRefreshToken(1L, "user");

        StepVerifier.create(jwtService.extractAuthorities(refreshToken))
                .expectNextMatches(List::isEmpty)
                .verifyComplete();
    }

    @Test
    void isRefreshToken_ShouldReturnTrue_WhenTypeIsRefresh() {
        when(jwtProperties.getRefreshTokenExpirationMs()).thenReturn(REFRESH_EXPIRATION);
        String refreshToken = jwtService.generateRefreshToken(1L, "user");

        StepVerifier.create(jwtService.isRefreshToken(refreshToken))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void isRefreshToken_ShouldReturnFalse_WhenTypeIsAccess() {
        when(jwtProperties.getAccessTokenExpirationMs()).thenReturn(ACCESS_EXPIRATION);
        String accessToken = jwtService.generateAccessToken(1L, "user", List.of());

        StepVerifier.create(jwtService.isRefreshToken(accessToken))
                .expectNext(false)
                .verifyComplete();
    }
}