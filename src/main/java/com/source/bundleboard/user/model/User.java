package com.source.bundleboard.user.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.Set;

@Table("users")
public record User(
        @Id Long id,

        String username,

        String email,

        @Column("password_hash")
        String passwordHash,

        @Column("avatar_url")
        String avatarUrl,

        Set<UserRole> roles,

        UserStatus status,

        Instant lastLoginAt,

        Instant createdAt
) {
}
