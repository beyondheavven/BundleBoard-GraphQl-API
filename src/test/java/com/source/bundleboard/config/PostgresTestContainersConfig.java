package com.source.bundleboard.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Map;

public class PostgresTestContainersConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String REUSE_PROPERTY = "${testcontainers.reuse.enable}";

    private static final String POSTGRES_IMAGE = "postgres:15-alpine";

    private static final String DB_USERNAME = "bundleboard_user";

    private static final String DB_PASSWORD = "bundleboard_password";

    private static final String DB_NAME = "bundleboard_db";

    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(POSTGRES_IMAGE)
            .withDatabaseName(DB_NAME)
            .withUsername(DB_USERNAME)
            .withPassword(DB_PASSWORD)
            .withReuse(Boolean.parseBoolean(System.getProperty(REUSE_PROPERTY, "true")));


    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        if (!postgres.isRunning()) {
            postgres.start();
        }

        String r2dbcUrl = String.format("r2dbc:postgresql://%s:%d/%s",
                postgres.getHost(),
                postgres.getFirstMappedPort(),
                postgres.getDatabaseName());

        Map<String, Object> testProperties = Map.of(
                "spring.r2dbc.url", r2dbcUrl,
                "spring.r2dbc.username", postgres.getUsername(),
                "spring.r2dbc.password", postgres.getPassword(),
                "POSTGRES_PORT", postgres.getFirstMappedPort(),
                "POSTGRES_URL", postgres.getHost(),
                "POSTGRES_DB", postgres.getDatabaseName()
        );

        applicationContext.getEnvironment().getPropertySources().addFirst(
                new MapPropertySource("testcontainers-properties", testProperties)
        );

    }
}
