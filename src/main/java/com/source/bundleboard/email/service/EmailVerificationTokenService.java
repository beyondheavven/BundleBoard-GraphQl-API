package com.source.bundleboard.email.service;

import com.source.bundleboard.email.dto.EmailResponse;
import reactor.core.publisher.Mono;

public interface EmailVerificationTokenService {

    Mono<EmailResponse> verifyEmail(String tokenValue);

    Mono<EmailResponse> sendChangeEmailToken(String newEmail, String name);

    Mono<EmailResponse> resendVerificationEmail(String email);

    String sha256Hex(String value);

    String generateRawToken();
}
