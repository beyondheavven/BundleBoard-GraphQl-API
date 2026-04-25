package com.source.bundleboard.email.mail.service;

import com.source.bundleboard.email.mail.propeties.MailProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    private final SpringTemplateEngine templateEngine;

    private final MailProperties mailProperties;

    @Override
    public Mono<Void> sendVerificationEmail(String toEmail, String verificationToken) {
        return null;
    }

    @Override
    public Mono<Void> sendPasswordResetEmail(String toEmail, String code) {
        return null;
    }
}
