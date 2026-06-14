package com.source.bundleboard.config;

import com.source.bundleboard.config.properties.R2dbcProperties;
import com.source.bundleboard.mediaresource.model.MimeType;
import com.source.bundleboard.user.model.UserRole;
import com.source.bundleboard.user.model.UserStatus;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.postgresql.codec.EnumCodec;
import io.r2dbc.spi.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import java.time.Duration;

@Configuration
@EnableR2dbcRepositories
@RequiredArgsConstructor
public class R2dbcConfig extends AbstractR2dbcConfiguration {

    private final R2dbcProperties r2dbcProperties;

    @Bean
    @Override
    public ConnectionFactory connectionFactory() {

        PostgresqlConnectionFactory postgresqlConnectionFactory = new PostgresqlConnectionFactory(
                PostgresqlConnectionConfiguration.builder()
                        .host(extractHost(r2dbcProperties.getUrl()))
                        .port(6543)
                        .database(extractDatabase(r2dbcProperties.getUrl()))
                        .username(r2dbcProperties.getUsername())
                        .password(r2dbcProperties.getPassword())
                        .preparedStatementCacheQueries(0)
                        .codecRegistrar(EnumCodec.builder()
                                .withEnum("media_mime_type", MimeType.class)
                                .withEnum("user_role", UserRole.class)
                                .withEnum("user_status", UserStatus.class)
                                .build())
                        .build()
        );

        ConnectionPoolConfiguration poolConfiguration = ConnectionPoolConfiguration.builder(postgresqlConnectionFactory)
                .maxSize(10)
                .initialSize(2)
                .maxIdleTime(Duration.ofMinutes(30))
                .validationQuery("SELECT 1")
                .build();

        return new ConnectionPool(poolConfiguration);
    }

    private String extractHost(String url) {
        String cleanUrl = url.replace("r2dbc:pool:postgresql://", "").replace("r2dbc:postgresql://", "");
        return cleanUrl.split(":")[0];
    }

    private String extractDatabase(String url) {
        int qIndex = url.indexOf("?");
        String cleanUrl = qIndex != -1 ? url.substring(0, qIndex) : url;
        return cleanUrl.substring(cleanUrl.lastIndexOf("/") + 1);
    }
}