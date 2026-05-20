package com.source.bundleboard.auth;

import com.source.bundleboard.AbstractIntegrationTest;
import com.source.bundleboard.user.model.User;
import com.source.bundleboard.user.model.UserRole;
import com.source.bundleboard.user.model.UserStatus;
import com.source.bundleboard.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AuthServiceIT extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setupUser() {
        String rawPassword = "password123";
        testUser = new User(
                null, "testuser", "test@test.com", passwordEncoder.encode(rawPassword),
                "", Set.of(UserRole.client), UserStatus.active,
                Instant.now(), Instant.now(), true
        );
        userRepository.save(testUser).block();
    }

    @Test
    void login_ShouldReturnAuthResponse_WhenCredentialsAreValid() {
        String mutation = """
            mutation {
                login(input: { username: "testuser", password: "password123" }) {
                    accessToken
                    refreshToken
                    user {
                        username
                    }
                }
            }
            """;

        graphQlTester.document(mutation)
                .execute()
                .path("login")
                .entity(Map.class)
                .satisfies(response -> {
                    assertThat(response.get("accessToken")).isNotNull();
                    assertThat(response.get("refreshToken")).isNotNull();
                    Map<String, String> user = (Map<String, String>) response.get("user");
                    assertThat(user.get("username")).isEqualTo("testuser");
                });
    }

    @Test
    void register_ShouldCreateUserInDatabase() {
        String mutation = """
            mutation {
                register(input: { username: "newuser", email: "new@test.com", password: "password123" }) {
                    user {
                        username
                    }
                }
            }
            """;

        graphQlTester.document(mutation)
                .execute()
                .path("register.user.username")
                .entity(String.class)
                .isEqualTo("newuser");

        var savedUser = userRepository.findByUsername("newuser").block();
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("new@test.com");
    }
}
