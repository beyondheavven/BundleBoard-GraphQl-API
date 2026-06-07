package com.source.bundleboard.email.mail;

import com.source.bundleboard.api.exception.BadTemplateEmailException;
import com.source.bundleboard.email.mail.propeties.MailProperties;
import com.source.bundleboard.email.mail.service.MailServiceImpl;
import com.source.bundleboard.email.properties.EmailVerificationProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private SpringTemplateEngine templateEngine;

    @Mock
    private MailProperties mailProperties;

    @Mock
    private EmailVerificationProperties emailVerificationProperties;

    @InjectMocks
    private MailServiceImpl mailService;

    @Mock
    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        lenient().when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        lenient().when(mailProperties.getFrom()).thenReturn("noreply@bundleboard.com");
        lenient().when(mailProperties.getFromName()).thenReturn("BundleBoard");
    }

    @Test
    void sendTemplateEmailSync_Success() {
        String toEmail = "user@test.com";
        String subject = "Test Subject";
        String template = "test-template";
        Map<String, Object> model = Map.of("key", "value");

        when(templateEngine.process(eq(template), any(Context.class)))
                .thenReturn("<html>Content</html>");

        assertDoesNotThrow(() -> mailService.sendTemplateEmailSync(toEmail, subject, template, model));
        verify(mailSender).send(mimeMessage);
        verify(templateEngine).process(eq(template), any(Context.class));
    }

    @Test
    void sendTemplateEmailSync_ThrowsBadTemplateEmailException_WhenMessagingExceptionOccurs() {
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("content");

        doAnswer(invocation -> {
            throw new MessagingException("SMTP Connection failed");
        }).when(mailSender).send(any(MimeMessage.class));
        assertThrows(BadTemplateEmailException.class, () ->
                mailService.sendTemplateEmailSync("user@test.com", "Sub", "tpl", Map.of())
        );
    }

    @Test
    void buildLink_ShouldReturnCorrectUrl() {
        String baseUrl = "https://bundleboard.com";
        String path = "/verify";
        String token = "abc123";
        when(emailVerificationProperties.getVerifyEmailUrl()).thenReturn(baseUrl);
        String result = mailService.buildLink(path, token);
        assertEquals("https://bundleboard.com/verify?token=abc123", result);
    }
}