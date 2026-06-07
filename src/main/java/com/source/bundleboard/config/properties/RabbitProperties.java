package com.source.bundleboard.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "spring.rabbitmq")
@Validated
public class RabbitProperties {

    @NotBlank(message = "RabbitMQ host is required")
    private String host;

    @NotNull(message = "RabbitMQ port is required")
    private Integer port;

    @NotBlank(message = "RabbitMQ username is required")
    private String username;

    @NotBlank(message = "RabbitMQ password is required")
    private String password;

    @NotBlank(message = "RabbitMQ mediaQueue is required")
    private String mediaQueue;

    @NotBlank(message = "RabbitMQ emailQueue is required")
    private String emailQueue;

    @NotBlank(message = "RabbitMQ webhookQueue is required")
    private String webhookQueue;
}
