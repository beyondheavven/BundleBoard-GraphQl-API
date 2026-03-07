package com.source.bundleboard.user.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.Set;

@Table("users")
public record User(
        @Id Long id,

        @Column("username")
        String username,

        @Column("email")
        String email,

        @Column("password_hash")
        String passwordHash,

        @Column("avatar_url")
        String avatarUrl,

        @Column("roles")
        Set<UserRole> roles,

        @Column("status")
        UserStatus status,

        @Column("last_login_at")
        Instant lastLoginAt,

        @Column("created_at")
        Instant createdAt
) {
}
