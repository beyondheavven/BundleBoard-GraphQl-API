package com.source.bundleboard.config;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLR2DBCDatabaseContainer;

@TestConfiguration(proxyBeanMethods = false)
public class PostgresTestContainersConfig {

    private static final String POSTGRES_IMAGE = "postgres:15-alpine";

    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>(POSTGRES_IMAGE)
                    .withUrlParam("stringtype", "unspecified");

    static {
        postgresContainer.start();
    }

    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgresContainer() {
        return postgresContainer;
    }

    @Bean
    @ServiceConnection
    public PostgreSQLR2DBCDatabaseContainer r2dbcContainer() {
        return new PostgreSQLR2DBCDatabaseContainer(postgresContainer);
    }

}