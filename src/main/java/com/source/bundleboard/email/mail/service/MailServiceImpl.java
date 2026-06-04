package com.source.bundleboard.email.mail.service;

import com.source.bundleboard.api.exception.BadTemplateEmailException;
import com.source.bundleboard.email.properties.EmailVerificationProperties;
import com.source.bundleboard.email.mail.propeties.MailProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    private final SpringTemplateEngine templateEngine;

    private final MailProperties mailProperties;

    private final EmailVerificationProperties emailVerificationProperties;

    private final static String UTF_8 = "UTF-8";

    @Override
    public Mono<Void> sendVerificationEmail(String toEmail, String verificationToken) {
        String link = buildLink(mailProperties.getPaths().getVerificationEmail(), verificationToken);
        return sendTemplateEmail(
                toEmail,
                mailProperties.getSubjects().getVerificationEmail(),
                mailProperties.getTemplates().getVerificationEmail(),
                Map.of("verificationLink", link)
        );
    }

    @Override
    public Mono<Void> sendPasswordChangeEmail(String toEmail, String code) {
        return sendTemplateEmail(
                toEmail,
                mailProperties.getSubjects().getResetPassword(),
                mailProperties.getTemplates().getResetPassword(),
                Map.of("resetCode", code)
        );
    }

    @Override
    public Mono<Void> sendPasswordResetLink(String toEmail, String link) {
        return sendTemplateEmail(
                toEmail,
                mailProperties.getSubjects().getResetPassword(),
                mailProperties.getTemplates().getResetPassword(),
                Map.of("resetLink", link)
        );
    }

    @Override
    public Mono<Void> sendPurchaseReceipt(String toEmail, String username, BigDecimal amount, String currency) {
        return sendTemplateEmail(
                toEmail,
                mailProperties.getSubjects().getPurchaseReceipt(),
                mailProperties.getTemplates().getPurchaseReceipt(),
                Map.of(
                        "username", username,
                        "amount", amount,
                        "currency", currency
                )
        );
    }

    private Mono<Void> sendTemplateEmail(String toEmail, String subject, String template, Map<String, Object> model) {
        return Mono.fromRunnable(() -> {
            try {
                Context context = new Context();
                context.setVariables(model);

                String html = templateEngine.process(template, context);

                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, UTF_8);

                helper.setFrom(mailProperties.getFrom());
                helper.setTo(toEmail);
                helper.setSubject(subject);
                helper.setText(html, true);

                mailSender.send(mimeMessage);
                log.info("Email successfully sent to {}", toEmail);
            } catch (MessagingException e) {
                throw new BadTemplateEmailException(e.getMessage());
            }
        })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    private String buildLink(String path, String token){
        return UriComponentsBuilder.fromUriString(emailVerificationProperties.getVerifyEmailUrl())
                .path(path)
                .queryParam("token", token)
                .build()
                .toUriString();
    }
}
