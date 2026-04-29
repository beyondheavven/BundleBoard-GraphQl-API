package com.source.bundleboard.email.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.email-verification")
public class EmailVerificationProperties {

    private Long ttlMs;

    private String verifyEmailUrl;

    private String hashAlgorithm;

    private String hexFormat;

    private int hashLength;

    private int byteLength;

    private int maxAttempts;

    private int blockDurationSeconds;

}
