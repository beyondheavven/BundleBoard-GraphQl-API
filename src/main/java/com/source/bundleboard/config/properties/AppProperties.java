package com.source.bundleboard.config.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "app.cors")
@Validated
public class AppProperties {

    @NotEmpty(message = "CORS Allowed Origins list cannot be empty")
    private List<String> allowedOrigins;

    @NotEmpty(message = "CORS Allowed Methods list cannot be empty")
    private List<String> allowedMethods;

    @NotEmpty(message = "CORS Allowed Headers list cannot be empty")
    private List<String> allowedHeaders;

    @NotNull(message = "CORS Exposed Headers list cannot be null (can be empty)")
    private List<String> exposedHeaders;

    private boolean allowCredentials;

    @NotNull(message = "CORS Max Age is required")
    @Min(value = 0, message = "CORS Max Age cannot be negative")
    private Long maxAge;

}
