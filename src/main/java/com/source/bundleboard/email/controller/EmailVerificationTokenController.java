package com.source.bundleboard.email.controller;

import com.source.bundleboard.email.dto.EmailResponse;
import com.source.bundleboard.email.service.EmailVerificationTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class EmailVerificationTokenController {

    private final EmailVerificationTokenService tokenService;

    @MutationMapping
    @PreAuthorize("permitAll()")
    public Mono<EmailResponse> verifyEmail(@Argument String token) {
        log.info("🎯 [TEST] Попытка верификации токена: {}", token);
        return tokenService.verifyEmail(token);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Mono<EmailResponse> requestEmailChange(@Argument String newEmail, Principal principal) {
        return tokenService.sendChangeEmailToken(newEmail, principal.getName());
    }

    @MutationMapping
    @PreAuthorize("permitAll()")
    public Mono<EmailResponse> resendVerificationEmail(@Argument String email){
        return tokenService.resendVerificationEmail(email);
    }

}
