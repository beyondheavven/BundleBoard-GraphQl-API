package com.source.bundleboard;

import com.source.bundleboard.config.PostgresTestContainersConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.graphql.test.autoconfigure.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = PostgresTestContainersConfig.class)
@ActiveProfiles("test")
@Testcontainers
@AutoConfigureHttpGraphQlTester
public abstract class AbstractControllerIntegrationTest {

    @Autowired
    protected HttpGraphQlTester graphQlTester;

}
