package com.source.bundleboard.email.mail.service;

import reactor.core.publisher.Mono;

public interface MailService {

    Mono<Void> sendVerificationEmail(String toEmail, String verificationToken);

    Mono<Void> sendPasswordChangeEmail(String toEmail, String code);

    Mono<Void> sendPasswordResetLink(String toEmail, String link);

}
