package com.source.bundleboard.email.service;

import reactor.core.publisher.Mono;

public interface EmailVerificationTokenService {

    Mono<Boolean> verifyEmail(String tokenValue);

    Mono<Boolean> sendChangeEmailToken(String newEmail, String name);

    Mono<Boolean> resendVerificationEmail(String email);
}
