package com.source.bundleboard.auth.controller;

import com.source.bundleboard.auth.dto.AuthRequest;
import com.source.bundleboard.auth.dto.AuthResponse;
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
    public Mono<AuthResponse> login(@Argument AuthRequest request) {
        return authService.authenticate(request)
                .onErrorResume(e -> Mono.error(new RuntimeException(e.getMessage())));
    }


}
