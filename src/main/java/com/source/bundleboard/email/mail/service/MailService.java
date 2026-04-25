package com.source.bundleboard.email.mail.service;

import reactor.core.publisher.Mono;

public interface MailService {

    Mono<Void> sendVerificationEmail(String toEmail, String verificationToken);

    Mono<Void> sendPasswordResetEmail(String toEmail, String code);

}
