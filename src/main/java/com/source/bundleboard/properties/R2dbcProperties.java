package com.source.bundleboard.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.r2dbc")
public class R2dbcProperties {

    private String url;

    private String username;

    private String password;
}
