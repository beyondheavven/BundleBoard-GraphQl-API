package com.source.bundleboard.refreshtoken.model;

import org.springframework.data.annotation.Id;

import java.time.Instant;

public record RefreshToken(
        @Id
        Long id,

        Long userId,

        String token,

        Instant issuedAt,

        Instant expirationTime
) {
}
