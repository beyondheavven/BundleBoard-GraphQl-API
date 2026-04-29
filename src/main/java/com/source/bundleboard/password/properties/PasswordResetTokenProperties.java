package com.source.bundleboard.password.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.password-verification")
public class PasswordResetTokenProperties {

    private int maxAttempts;

    private int blockDurationSeconds;

}
