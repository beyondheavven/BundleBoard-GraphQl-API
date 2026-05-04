package com.source.bundleboard.auth.service;

import com.source.bundleboard.auth.dto.*;
import reactor.core.publisher.Mono;

public interface AuthService {

    Mono<AuthResponse> authenticate(AuthRequest request);

    Mono<AuthResponse> register(RegisterRequest request);

    Mono<AuthResponse> refreshToken(RefreshTokenRequest refreshTokenRequest);

    Mono<Boolean> logout(RefreshTokenRequest refreshTokenRequest);

    Mono<AuthResponse> socialLogin(SocialAuthRequest input);
}
