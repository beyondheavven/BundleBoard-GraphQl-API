package com.source.bundleboard.auth.service;

import com.source.bundleboard.auth.dto.AuthRequest;
import com.source.bundleboard.auth.dto.AuthResponse;
import com.source.bundleboard.auth.dto.RefreshTokenRequest;
import com.source.bundleboard.auth.dto.RegisterRequest;
import reactor.core.publisher.Mono;

public interface AuthService {

    Mono<AuthResponse> authenticate(AuthRequest request);

    Mono<AuthResponse> register(RegisterRequest request);

    Mono<AuthResponse> refreshToken(RefreshTokenRequest refreshTokenRequest);

    Mono<Void> logout(RefreshTokenRequest refreshTokenRequest);




}
