package com.source.bundleboard.jwt;

import com.source.bundleboard.auth.core.ReactiveUserDetailsServiceImpl;
import com.source.bundleboard.auth.jwt.JwtAuthenticationFilter;
import com.source.bundleboard.auth.jwt.service.JwtService;
import io.jsonwebtoken.impl.DefaultClaims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private ReactiveUserDetailsServiceImpl userDetailsService;

    @Mock
    private WebFilterChain chain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    private UserDetails dummyUserDetails;

    @BeforeEach
    void setUp() {
        dummyUserDetails = User.withUsername("testuser").password("pass").roles("USER").build();
    }

    @Test
    void filter_OptionsRequest_ShouldSkipTokenLogic() {
        MockServerHttpRequest request = MockServerHttpRequest.options("/api/data").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(chain.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        verifyNoInteractions(jwtService);
        verifyNoInteractions(userDetailsService);
    }

    @Test
    void filter_NoAuthHeader_ShouldSkipTokenLogic() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/data").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(chain.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        verifyNoInteractions(jwtService);
    }

    @Test
    void filter_ValidTokenWithUserId_ShouldAuthenticate() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/data")
                .header(HttpHeaders.AUTHORIZATION, "Bearer valid.token.here")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        DefaultClaims claims = new DefaultClaims(Map.of("userId", 1, "sub", "testuser"));

        when(jwtService.validateToken("valid.token.here")).thenReturn(Mono.just(claims));
        when(userDetailsService.loadUserById(1L)).thenReturn(Mono.just(dummyUserDetails));
        when(jwtService.extractAuthorities("valid.token.here")).thenReturn(Mono.just(Collections.emptyList()));
        when(chain.filter(any())).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        verify(userDetailsService).loadUserById(1L);
        verify(userDetailsService, never()).findByUsername(anyString());
    }

    @Test
    void filter_ValidLegacyTokenWithoutUserId_ShouldFallbackToUsername() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/data")
                .header(HttpHeaders.AUTHORIZATION, "Bearer legacy.token.here")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        DefaultClaims claims = new DefaultClaims(Map.of("sub", "legacyuser")); // No userId

        when(jwtService.validateToken("legacy.token.here")).thenReturn(Mono.just(claims));
        when(userDetailsService.findByUsername("legacyuser")).thenReturn(Mono.just(dummyUserDetails));
        when(jwtService.extractAuthorities("legacy.token.here")).thenReturn(Mono.just(Collections.emptyList()));
        when(chain.filter(any())).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        verify(userDetailsService).findByUsername("legacyuser");
        verify(userDetailsService, never()).loadUserById(anyLong());
    }

    @Test
    void filter_InvalidToken_ShouldResumeWithoutAuth() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/data")
                .header(HttpHeaders.AUTHORIZATION, "Bearer invalid.token")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(jwtService.validateToken("invalid.token")).thenReturn(Mono.error(new RuntimeException("Invalid token")));
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        verify(chain).filter(exchange);
    }
}