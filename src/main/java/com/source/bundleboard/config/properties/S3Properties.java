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
@ConfigurationProperties(prefix = "app.s3")
@Validated
public class S3Properties {

    @NotBlank(message = "S3 Endpoint is required")
    private String endpoint;

    @NotBlank(message = "S3 Access Key is required")
    private String accessKey;

    @NotBlank(message = "S3 Secret Key is required")
    private String secretKey;

    @NotBlank(message = "S3 Bucket Name is required")
    private String bucket;

    @NotBlank(message = "S3 Region is required")
    private String region;

    @NotBlank(message = "S3 Public URL Prefix is required")
    private String publicUrlPrefix;
}
