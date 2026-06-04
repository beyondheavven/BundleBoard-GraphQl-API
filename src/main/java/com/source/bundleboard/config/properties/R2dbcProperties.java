package com.source.bundleboard.config.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.r2dbc")
@Validated
public class R2dbcProperties {

    @NotBlank(message = "R2DBC Connection URL is required")
    private String url;

    @NotBlank(message = "R2DBC Username is required")
    private String username;

    @NotBlank(message = "R2DBC Password is required")
    private String password;
}
