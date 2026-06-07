package com.source.bundleboard.email.mail.service;

import com.source.bundleboard.api.exception.BadTemplateEmailException;
import com.source.bundleboard.email.mail.propeties.MailProperties;
import com.source.bundleboard.email.properties.EmailVerificationProperties;
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

import java.io.UnsupportedEncodingException;
import java.util.Map;


@Service
@AllArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    private final SpringTemplateEngine templateEngine;

    private final MailProperties mailProperties;

    private final static String UTF_8 = "UTF-8";

    private final EmailVerificationProperties emailVerificationProperties;

    public void sendTemplateEmailSync(String toEmail, String subject, String template, Map<String, Object> model) {
        try {
            Context context = new Context();
            context.setVariables(model);

            String html = templateEngine.process(template, context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, UTF_8);

            helper.setFrom(mailProperties.getFrom(), mailProperties.getFromName());
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(mimeMessage);
            log.info("Email successfully sent via RabbitMQ to {}", toEmail);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Failed to send email to {}", toEmail, e);
            throw new BadTemplateEmailException(e.getMessage());
        }
    }

    public String buildLink(String path, String token){
        return UriComponentsBuilder.fromUriString(emailVerificationProperties.getVerifyEmailUrl())
                .path(path)
                .queryParam("token", token)
                .build()
                .toUriString();
    }
}
