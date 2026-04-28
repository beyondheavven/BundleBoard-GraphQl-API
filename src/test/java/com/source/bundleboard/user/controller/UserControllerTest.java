package com.source.bundleboard.user.controller;

import com.source.bundleboard.AbstractControllerIntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class UserControllerTest extends AbstractControllerIntegrationTest {

    @Test
    void shouldReturnCurrentUserInfo_WhenAuthenticated() {

        String token = login("testuser", "password12345678");

        String query = """
            query {
              me {
                id
                username
                email
              }
            }
            """;

        execute(query, null, token)
                .errors().verify()
                .path("me.username")
                .entity(String.class)
                .isEqualTo("testuser");
    }

    @Test
    void shouldUpdateUserProfile_WhenAuthorized() {
        String token = login("testuser", "password123");

        String query = """
            mutation($input: UpdateUserRequest!) {
              updateMe(input: $input) {
                id
                username
                message
              }
            }
            """;

        Map<String, Object> variables = Map.of(
                "input", Map.of(
                        "username", "new_awesome_name"
                )
        );

        execute(query, variables, token)
                .errors().verify()
                .path("updateMe.username")
                .entity(String.class)
                .isEqualTo("new_awesome_name");
    }

    @Test
    void shouldReturnError_WhenFetchingMeWithoutAuth() {
        String query = "{ me { id } }";
        execute(query, null, null)
                .errors()
                .expect(error -> "Unauthorized".equals(error.getMessage()))
                .verify();
    }
}
