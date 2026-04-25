package com.source.bundleboard.email.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Table("email_verification_token")
public class EmailVerificationToken {

    @Id
    private Long id;

    @Column("users_id")
    private Long userId;

    @Column("token")
    private String token;

    @Column("token_type")
    private TokenType tokenType;

    @Column("token_status")
    private TokenStatus tokenStatus;

    @Column("expires_at")
    private Instant expiresAt;

    @Column("created_at")
    private Instant createdAt;
}
