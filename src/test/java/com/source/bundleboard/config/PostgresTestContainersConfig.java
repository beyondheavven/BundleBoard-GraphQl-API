package com.source.bundleboard.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.springframework.context.annotation.Bean;
import org.testcontainers.utility.MountableFile;

@TestConfiguration(proxyBeanMethods = false)
public class PostgresTestContainersConfig {

    private static final String REUSE_PROPERTY = "${testcontainers.reuse.enable}";

    private static final String POSTGRES_IMAGE = "postgres:15-alpine";

    private static final String POSTGRES_USER = "postgres";

    private static final String POSTGRES_PASSWORD = "test";

    private static final String POSTGRES_DB = "bundleboard";

    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgreSQLContainer(@Value(REUSE_PROPERTY) boolean reuse) {
        return new PostgreSQLContainer<>(POSTGRES_IMAGE)
                .withUsername(POSTGRES_USER)
                .withPassword(POSTGRES_PASSWORD)
                .withDatabaseName(POSTGRES_DB)
                .withReuse(reuse)
                .withCopyFileToContainer(
                        MountableFile.forHostPath("supabase/migrations"),
                     "/docker-entrypoint-initdb.d/"
                );
    }
}
