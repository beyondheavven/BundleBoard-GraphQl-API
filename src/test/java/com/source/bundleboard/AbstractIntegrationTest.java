package com.source.bundleboard;

import com.source.bundleboard.config.PostgresTestContainersConfig;
import com.source.bundleboard.email.mail.service.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Import(PostgresTestContainersConfig.class)
public abstract class AbstractIntegrationTest {

    @MockitoBean
    protected MailService mailService;

    @Autowired
    protected WebTestClient webTestClient;

    @Autowired
    protected DatabaseClient databaseClient;

    protected HttpGraphQlTester graphQlTester;

    @BeforeEach
    void setUp() {
        this.graphQlTester = HttpGraphQlTester.builder(webTestClient.mutate())
                .build();

        cleanDatabase().block();
    }

    protected HttpGraphQlTester authorizedGraphQlTester(String token) {
        return graphQlTester.mutate()
                .header("Authorization", "Bearer " + token)
                .build();
    }

    private Mono<Void> cleanDatabase() {
        return databaseClient.sql("TRUNCATE TABLE users, refresh_tokens RESTART IDENTITY CASCADE")
                .then();
    }

}
