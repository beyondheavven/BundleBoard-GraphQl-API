package com.source.bundleboard.auth.jwt.service;

import com.source.bundleboard.auth.jwt.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class JwtServiceImpl implements JwtService {

    private final JwtProperties jwtProperties;

    private final SecretKey signingKey;

    public JwtServiceImpl(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // Generate access token with user details and roles embedded as claims
    @Override
    public String generateAccessToken(String username, Collection<? extends GrantedAuthority> authorities) {
        Instant now = Instant.now();

        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .claim("type", "access")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(jwtProperties.getAccessTokenExpirationMs())))
                .signWith(signingKey)
                .compact();
    }

    // Generate refresh token for obtaining new access tokens
    @Override
    public String generateRefreshToken(String username) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .claim("type", "refresh")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(jwtProperties.getRefreshTokenExpirationMs())))
                .signWith(signingKey)
                .compact();
    }

    // Validate token and return claims wrapped in Mono for reactive processing
    @Override
    public Mono<Claims> validateToken(String token) {
        return Mono.fromCallable(() ->{
            try {
                return Jwts.parser()
                        .verifyWith(signingKey)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();
            } catch (Exception e) {
                throw new RuntimeException("Invalid JWT token: " + e.getMessage());
            }
        });
    }

    // Extract username from token claims
    @Override
    public Mono<String> extractUsername(String token) {
        return validateToken(token).map(Claims::getSubject);
    }


    // Extract authorities from token claims for security context
    @Override
    public Mono<List<GrantedAuthority>> extractAuthorities(String token) {
        return validateToken(token).map(claims -> {
            List<String> roles = claims.get("roles", List.class);
            if (roles == null || roles.isEmpty()) {
                return List.of();
            }
            return roles.stream()
                    .map(role -> new SimpleGrantedAuthority(role))
                    .collect(Collectors.toList());
        });
    }

    // Check if the token is a refresh token
    @Override
    public Mono<Boolean> isRefreshToken(String token) {
        return validateToken(token).map(claims -> "refresh".equals(claims.get("type", String.class)));
    }
}
