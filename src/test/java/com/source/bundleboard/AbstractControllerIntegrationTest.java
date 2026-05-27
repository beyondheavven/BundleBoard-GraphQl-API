package com.source.bundleboard;

import com.source.bundleboard.config.PostgresTestContainersConfig;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.graphql.test.tester.WebGraphQlTester;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(PostgresTestContainersConfig.class)
@ActiveProfiles("test")
@Testcontainers
@AutoConfigureWebTestClient
public abstract class AbstractControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    protected WebGraphQlTester graphQlTester;

    @BeforeEach
    void setup() {
        graphQlTester = HttpGraphQlTester.create(webTestClient);
    }


}
