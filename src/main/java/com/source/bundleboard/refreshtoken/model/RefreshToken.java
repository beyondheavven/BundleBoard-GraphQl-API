package com.source.bundleboard.refreshtoken.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.time.Instant;

public record RefreshToken(
        @Id
        Long id,

        @Column("users_id")
        Long userId,

        String token,

        Instant issuedAt,

        Instant expirationTime
) {
}
