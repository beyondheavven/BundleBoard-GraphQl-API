package com.source.bundleboard.email.mail.service;

import com.source.bundleboard.api.exception.BadTemplateEmailException;
import com.source.bundleboard.email.mail.propeties.MailProperties;
import com.source.bundleboard.email.properties.EmailVerificationProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@AllArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {

    private final SpringTemplateEngine templateEngine;

    private final MailProperties mailProperties;

    private final RestTemplate restTemplate = new RestTemplate();

    private final EmailVerificationProperties emailVerificationProperties;

    public void sendTemplateEmailSync(String toEmail, String subject, String template, Map<String, Object> model) {
        try {
            Context context = new Context();
            context.setVariables(model);

            String html = templateEngine.process(template, context);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.set("Authorization", "Bearer " + mailProperties.getApiKey());
            String fromSender = String.format("%s <%s>", mailProperties.getFromName(), mailProperties.getFrom());
            Map<String, Object> body = new HashMap<>();
            body.put("from", fromSender);
            body.put("to", List.of(toEmail));
            body.put("subject", subject);
            body.put("html", html);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, httpHeaders);
            restTemplate.postForEntity("https://api.resend.com/emails", request, String.class);
            log.info("Email successfully sent via Resend API (triggered by RabbitMQ) to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send email via Resend API to {}", toEmail, e);
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
