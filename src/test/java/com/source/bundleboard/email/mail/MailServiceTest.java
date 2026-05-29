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
import reactor.test.StepVerifier;

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

    @Mock private MimeMessage mimeMessage;

    private final MailProperties.Paths paths = new MailProperties.Paths();
    private final MailProperties.Subjects subjects = new MailProperties.Subjects();
    private final MailProperties.Templates templates = new MailProperties.Templates();

    @BeforeEach
    void setUp() {
        lenient().when(mailProperties.getPaths()).thenReturn(paths);
        lenient().when(mailProperties.getSubjects()).thenReturn(subjects);
        lenient().when(mailProperties.getTemplates()).thenReturn(templates);
        lenient().when(mailProperties.getFrom()).thenReturn("noreply@bundleboard.com");

        lenient().when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }


    @Test
    void sendVerificationEmail_Success() {
        paths.setVerificationEmail("/verify");
        subjects.setVerificationEmail("Verify your email");
        templates.setVerificationEmail("verification-template");

        when(emailVerificationProperties.getVerifyEmailUrl()).thenReturn("https://bundleboard.com");
        when(templateEngine.process(eq("verification-template"), any(Context.class)))
                .thenReturn("<html>Verification Link</html>");

        StepVerifier.create(mailService.sendVerificationEmail("user@test.com", "token123"))
                .verifyComplete();

        verify(mailSender).send(mimeMessage);
        verify(templateEngine).process(eq("verification-template"), any(Context.class));
    }

    @Test
    void sendPasswordChangeEmail_Success() {
        subjects.setResetPassword("Password Reset Code");
        templates.setResetPassword("reset-code-template");

        when(templateEngine.process(eq("reset-code-template"), any(Context.class)))
                .thenReturn("<html>Your code: 1234</html>");
        StepVerifier.create(mailService.sendPasswordChangeEmail("user@test.com", "1234"))
                .verifyComplete();

        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendPasswordResetLink_Success() {
        subjects.setResetPassword("Password Reset Link");
        templates.setResetPassword("reset-link-template");

        when(templateEngine.process(eq("reset-link-template"), any(Context.class)))
                .thenReturn("<html>Click here to reset</html>");

        StepVerifier.create(mailService.sendPasswordResetLink("user@test.com", "https://reset-url"))
                .verifyComplete();

        verify(mailSender).send(mimeMessage);
    }


    @Test
    void sendTemplateEmail_ThrowsBadTemplateEmailException_WhenMessagingExceptionOccurs() throws Exception {
        subjects.setResetPassword("Fail Test");
        templates.setResetPassword("fail-template");

        when(templateEngine.process(eq("fail-template"), any(Context.class)))
                .thenReturn("<html>Fail</html>");

        doAnswer(invocation -> {
            throw new MessagingException("SMTP Connection failed");
        }).when(mailSender).send(any(MimeMessage.class));

        StepVerifier.create(mailService.sendPasswordResetLink("invalid-email", "https://reset-url"))
                .expectErrorMatches(throwable -> throwable instanceof BadTemplateEmailException
                        && throwable.getMessage().contains("SMTP Connection failed"))
                .verify();
    }
}
