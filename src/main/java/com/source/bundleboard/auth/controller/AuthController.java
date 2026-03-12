package com.source.bundleboard.auth.controller;

import com.source.bundleboard.auth.dto.AuthRequest;
import com.source.bundleboard.auth.dto.AuthResponse;
import com.source.bundleboard.auth.dto.RefreshTokenRequest;
import com.source.bundleboard.auth.dto.RegisterRequest;
import com.source.bundleboard.auth.service.AuthService;
import com.source.bundleboard.constants.AuthApiPaths;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping(AuthApiPaths.AUTH_PATH)
public class AuthController {

    private final AuthService authService;

    @PostMapping(AuthApiPaths.LOGIN_PATH)
    public Mono<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return authService.authenticate(request);
    }


    @PostMapping(AuthApiPaths.REGISTER_PATH)
    public Mono<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping(AuthApiPaths.REFRESH_TOKEN)
    public Mono<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshToken) {
        return authService.refreshToken(refreshToken);
    }

    @PostMapping(AuthApiPaths.LOGOUT_PATH)
    public Mono<Void> logout(@RequestBody RefreshTokenRequest refreshToken){
        return authService.logout(refreshToken);
    }

}
