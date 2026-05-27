package com.source.bundleboard.auth;

import com.source.bundleboard.AbstractControllerIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

class AuthControllerIntTest extends AbstractControllerIntegrationTest {

    @Test
    @DisplayName("Should successfully register a new user")
    void register_ShouldReturnAuthResponse_WhenValidRequest() {
        String document = """
            mutation Register($input: RegisterRequest!) {
                register(input: $input) {
                    accessToken
                    refreshToken
                    user {
                        username
                        email
                    }
                }
            }
        """;

        Map<String, Object> input = Map.of(
                "username", "testuser",
                "email", "testuser@example.com",
                "password", "strongPass123",
                "role", "USER"
        );

        graphQlTester.document(document)
                .variable("input", input)
                .execute()
                .errors().verify()
                .path("register.accessToken").hasValue()
                .path("register.refreshToken").hasValue()
                .path("register.user.username").entity(String.class).isEqualTo("testuser");
    }

}