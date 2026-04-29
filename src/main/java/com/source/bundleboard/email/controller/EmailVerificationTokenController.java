package com.source.bundleboard.email.controller;

import com.source.bundleboard.email.dto.EmailResponse;
import com.source.bundleboard.email.service.EmailVerificationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class EmailVerificationTokenController {

    private final EmailVerificationTokenService tokenService;

    @MutationMapping
    public Mono<EmailResponse> verifyEmail(@Argument String token) {
        return tokenService.verifyEmail(token);
    }

    @MutationMapping
    public Mono<EmailResponse> requestEmailChange(@Argument String newEmail, Principal principal) {
        return tokenService.sendChangeEmailToken(newEmail, principal.getName());
    }

    @MutationMapping
    public Mono<EmailResponse> resendVerificationEmail(@Argument String email){
        return tokenService.resendVerificationEmail(email);
    }




}
