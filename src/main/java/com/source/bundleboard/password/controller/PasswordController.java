package com.source.bundleboard.password.controller;

import com.source.bundleboard.password.dto.PasswordConfirmResetInput;
import com.source.bundleboard.password.dto.PasswordChangeInput;
import com.source.bundleboard.password.dto.PasswordResetInput;
import com.source.bundleboard.password.dto.PasswordChangeResponse;
import com.source.bundleboard.password.dto.PasswordResetResponse;
import com.source.bundleboard.password.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class PasswordController {

    private final PasswordService passwordService;

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Mono<PasswordChangeResponse> requestPasswordChange(@Argument PasswordChangeInput input, @AuthenticationPrincipal UserDetails user){
        return passwordService.requestPasswordChange(input, user);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Mono<PasswordChangeResponse> confirmPasswordChange(@Argument String code, @AuthenticationPrincipal UserDetails user){
        return passwordService.confirmPasswordChange(code, user);
    }

    @MutationMapping
    @PreAuthorize("permitAll()")
    public Mono<PasswordResetResponse> requestPasswordReset(@Argument PasswordResetInput input){
        return passwordService.requestPasswordReset(input);
    }

    @MutationMapping
    @PreAuthorize("permitAll()")
    public Mono<PasswordResetResponse> confirmPasswordReset(@Argument PasswordConfirmResetInput input){
        return passwordService.confirmPasswordReset(input);
    }
}
