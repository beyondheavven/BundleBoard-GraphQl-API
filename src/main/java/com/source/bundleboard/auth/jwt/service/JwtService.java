package com.source.bundleboard.auth.jwt.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.GrantedAuthority;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

public interface JwtService {

    String generateAccessToken(String username, Collection<? extends GrantedAuthority> authorities);

    String generateRefreshToken(String username);

    Mono<Claims> validateToken(String token);

    Mono<String> extractUsername(String token);

    Mono<List<GrantedAuthority>> extractAuthorities(String token);

    Mono<Boolean> isRefreshToken(String token);

}
