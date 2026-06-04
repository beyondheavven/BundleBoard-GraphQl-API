package com.source.bundleboard.config;

import com.source.bundleboard.config.properties.R2dbcProperties;
import com.source.bundleboard.user.model.UserRole;
import com.source.bundleboard.user.model.UserStatus;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.postgresql.codec.EnumCodec;
import io.r2dbc.spi.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcRepositories
@RequiredArgsConstructor
public class R2dbcConfig extends AbstractR2dbcConfiguration {

    private final R2dbcProperties r2dbcProperties;

    @Bean
    @Override
    public ConnectionFactory connectionFactory() {
        return new PostgresqlConnectionFactory(
                PostgresqlConnectionConfiguration.builder()
                        .host(extractHost(r2dbcProperties.getUrl()))
                        .port(5432)
                        .database(extractDatabase(r2dbcProperties.getUrl()))
                        .username(r2dbcProperties.getUsername())
                        .password(r2dbcProperties.getPassword())
                        .codecRegistrar(EnumCodec.builder()
                                .withEnum("user_role", UserRole.class)
                                .withEnum("user_status", UserStatus.class)
                                .build())
                        .build()
        );
    }

    private String extractHost(String url) {
        return url.split("//")[1].split(":")[0];
    }

    private String extractDatabase(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}