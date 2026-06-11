package com.source.bundleboard.auth.jwt;

import com.source.bundleboard.auth.core.ReactiveUserDetailsServiceImpl;
import com.source.bundleboard.auth.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;

    private final ReactiveUserDetailsServiceImpl userDetailsService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        if (HttpMethod.OPTIONS.equals(exchange.getRequest().getMethod())) {
            return chain.filter(exchange);
        }

        String token = extractToken(exchange.getRequest());

        if (token == null) {
            return chain.filter(exchange);
        }

        return jwtService.validateToken(token)
                .flatMap(claims -> {
                    Number userIdNumber = claims.get("userId", Number.class);
                    Mono<UserDetails> userDetailsMono;
                    if (userIdNumber != null) {
                        userDetailsMono = userDetailsService.loadUserById(userIdNumber.longValue());
                    } else {
                        String username = claims.getSubject();
                        log.info("ℹ️ Legacy token detected without userId claim. Falling back to username: [{}]", username);
                        userDetailsMono = userDetailsService.findByUsername(username);
                    }

                    return userDetailsMono.flatMap(userDetails ->
                            jwtService.extractAuthorities(token)
                                    .map(grantedAuthorities -> new UsernamePasswordAuthenticationToken(
                                            userDetails,
                                            null,
                                            grantedAuthorities
                                    ))
                    );
                })
                .flatMap(auth -> chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
                .onErrorResume(e -> {
                    log.warn("⚠️ Transactional authentication handshake rejected: {}", e.getMessage());
                    return chain.filter(exchange);
                });
    }

    private String extractToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
