package com.source.bundleboard.auth.controller;

import com.source.bundleboard.auth.dto.AuthRequest;
import com.source.bundleboard.auth.dto.AuthResponse;
import com.source.bundleboard.auth.dto.RefreshTokenRequest;
import com.source.bundleboard.auth.dto.RegisterRequest;
import com.source.bundleboard.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @MutationMapping
    public Mono<AuthResponse> login(@Argument AuthRequest input) {
        return authService.authenticate(input);
    }

    @MutationMapping
    public Mono<AuthResponse> register(@Argument RegisterRequest input) {
        return authService.register(input);
    }

    @MutationMapping
    public Mono<AuthResponse> refreshToken(@Argument RefreshTokenRequest input) {
        return authService.refreshToken(input);
    }

    @MutationMapping
    public Mono<Void> logout(@Argument RefreshTokenRequest input){
        return authService.logout(input);
    }

}
