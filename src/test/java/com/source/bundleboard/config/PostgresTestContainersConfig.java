package com.source.bundleboard.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration
public class PostgresTestContainersConfig {

    private static final String REUSE_PROPERTY = "${testcontainers.reuse.enable}";

    private static final String POSTGRES_IMAGE = "postgres:15-alpine";

    private static final String DB_USERNAME = "bundleboard_user";

    private static final String DB_PASSWORD = "bundleboard_password";

    private static final String DB_NAME = "bundleboard_db";

    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(POSTGRES_IMAGE)
            .withDatabaseName(DB_NAME)
            .withUsername(DB_USERNAME)
            .withPassword(DB_PASSWORD)
            .withReuse(Boolean.parseBoolean(System.getProperty(REUSE_PROPERTY, "true")));

    static {
        postgres.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () ->
                "r2dbc:postgresql://" + postgres.getHost() + ":" + postgres.getFirstMappedPort() + "/" + postgres.getDatabaseName()
        );
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.r2dbc.password", postgres::getPassword);
    }
}
