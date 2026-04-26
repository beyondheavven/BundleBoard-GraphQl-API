package com.source.bundleboard.email.dto;

import com.source.bundleboard.email.model.EmailVerificationToken;

public record TokenEntity(
        String rawToken,

        EmailVerificationToken token
) {
}
