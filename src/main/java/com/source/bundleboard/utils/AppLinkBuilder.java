package com.source.bundleboard.utils;

import com.source.bundleboard.email.properties.EmailVerificationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class AppLinkBuilder {

    private final EmailVerificationProperties emailVerificationProperties;

    public String buildLink(String path, String token) {
        return UriComponentsBuilder.fromUriString(emailVerificationProperties.getVerifyEmailUrl())
                .path(path)
                .queryParam("token", token)
                .build()
                .toUriString();
    }
}
