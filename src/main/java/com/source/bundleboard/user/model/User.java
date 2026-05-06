package com.source.bundleboard.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "users")
public class User {

    @Id
    @Column("id")
    private Long id;

    @Column("username")
    private String username;

    @Column("email")
    private String email;

    @Column("password_hash")
    private String passwordHash;

    @Column("avatar_url")
    private String avatarUrl;

    @Column("roles")
    private Set<UserRole> roles;

    @Column("status")
    private UserStatus status;

    @Column("last_login_at")
    private Instant lastLoginAt;

    @Column("created_at")
    private Instant createdAt;

    @Column("is_setup_completed")
    private boolean isSetupCompleted;
}
