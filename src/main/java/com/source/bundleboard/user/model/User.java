package com.source.bundleboard.user.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
public record User(
        @Id Long id,

        String username,

        String email,

        String passwordHash,

        String avatarUrl,

        UserRole role,

        UserStatus status
) {
}
