package com.source.bundleboard.auth.controller;

import com.source.bundleboard.auth.dto.*;
import com.source.bundleboard.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @MutationMapping
    @PreAuthorize("permitAll()")
    public Mono<AuthResponse> login(@Argument @Valid AuthRequest input) {
        return authService.authenticate(input);
    }

    @PreAuthorize("permitAll()")
    @MutationMapping
    public Mono<AuthResponse> register(@Argument @Valid RegisterRequest input) {
        return authService.register(input);
    }

    @PreAuthorize("permitAll()")
    @MutationMapping
    public Mono<RefreshResponse> refreshToken(@Argument @Valid RefreshTokenRequest input) {
        return authService.refreshToken(input);
    }

    @PreAuthorize("isAuthenticated()")
    @MutationMapping
    public Mono<Boolean> logout(@Argument @Valid RefreshTokenRequest input){
        return authService.logout(input);
    }

    @PreAuthorize("permitAll()")
    @MutationMapping
    public Mono<AuthResponse> socialLogin (@Argument @Valid SocialLoginRequest input){
        return authService.socialLogin(input);
    }

    @PreAuthorize("permitAll()")
    @MutationMapping
    public Mono<AuthResponse> socialRegister (@Argument @Valid SocialRegisterRequest input){
        return authService.socialRegister(input);
    }

}
