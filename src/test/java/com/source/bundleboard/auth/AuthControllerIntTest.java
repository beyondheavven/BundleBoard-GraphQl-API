package com.source.bundleboard.auth;

import com.source.bundleboard.AbstractControllerIntegrationTest;
import com.source.bundleboard.email.service.EmailVerificationTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;

class AuthControllerIntTest extends AbstractControllerIntegrationTest {

    @MockitoBean
    private EmailVerificationTokenService emailVerificationTokenService;

    @Test
    @DisplayName("Should successfully register a new user")
    void register_ShouldReturnAuthResponse_WhenValidRequest() {

        Mockito.when(emailVerificationTokenService.resendVerificationEmail(anyString()))
                .thenReturn(Mono.empty());

        String document = """
            mutation Register($input: RegisterRequest!) {
                register(input: $input) {
                    accessToken
                    refreshToken
                }
            }
        """;

        Map<String, Object> input = Map.of(
                "username", "testuser",
                "email", "testuser@example.com",
                "password", "strongPass123",
                "role", "client"
        );

        graphQlTester.document(document)
                .variable("input", input)
                .execute()
                .errors()
                .satisfy(errors -> {
                    if (!errors.isEmpty()) {
                        System.err.println("❌ GraphQL вернул ошибки: " + errors);
                    }
                })
                .path("register.accessToken").hasValue()
                .path("register.refreshToken").hasValue();
    }

}