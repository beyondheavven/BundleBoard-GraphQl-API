package com.source.bundleboard.password.controller;

import com.source.bundleboard.password.dto.*;
import com.source.bundleboard.password.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class PasswordController {

    private final PasswordService passwordService;

    @MutationMapping
    public Mono<PasswordChangeResponse> requestPasswordChange(@Argument PasswordChangeInput input, @AuthenticationPrincipal UserDetails user){
        return passwordService.requestPasswordChange(input, user);
    }

    @MutationMapping
    public Mono<PasswordChangeResponse> confirmPasswordChange(@Argument String code, @AuthenticationPrincipal UserDetails user){
        return passwordService.confirmPasswordChange(code, user);
    }

    public Mono<PasswordResetResponse> requestPasswordReset(@Argument PasswordResetInput input){
        return passwordService.requestPasswordReset(input);
    }

    public Mono<PasswordResetResponse> confirmPasswordReset(@Argument PasswordConfirmResetInput input){
        return passwordService.confirmPasswordReset(input);
    }
}
