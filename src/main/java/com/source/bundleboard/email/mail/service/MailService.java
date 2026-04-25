package com.source.bundleboard.email.mail.service;

import reactor.core.publisher.Mono;

public interface MailService {

    Mono<Void> sendVerificationEmail(String email, String token);

}
