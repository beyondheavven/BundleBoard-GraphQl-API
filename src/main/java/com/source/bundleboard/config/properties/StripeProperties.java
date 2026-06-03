package com.source.bundleboard.config.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Configuration
@Validated
@ConfigurationProperties(prefix = "stripe")
public class StripeProperties {

    @NotBlank(message = "Stripe Secret Key is required")
    private String secretKey;

    @NotBlank(message = "Stripe Public Key is required")
    private String publicKey;

    @NotBlank(message = "Stripe Webhook Secret is required")
    private String webhookSecret;

    @NotBlank(message = "Payment Success URL is required")
    private String paymentSuccessUrl;

    @NotBlank(message = "Payment Cancel URL is required")
    private String paymentCancelUrl;

}
