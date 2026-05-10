package com.source.bundleboard.password.model;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table("password_reset_tokens")
public class PasswordResetToken {

    @Id
    private Long id;

    @Column("user_id")
    private Long userId;

    @Column("code")
    private String code;

    @Column("new_password_hash")
    private String newPasswordHash;

    @Column("type")
    private PasswordResetType type;

    @Column("resend_count")
    private int resendCount;

    @Column("attempt_count")
    private int attemptCount;

    @Column("expires_at")
    private Instant expiresAt;

    @Column("created_at")
    private Instant createdAt;

    @Column("blocked_until")
    private Instant blockedUntil;
}
