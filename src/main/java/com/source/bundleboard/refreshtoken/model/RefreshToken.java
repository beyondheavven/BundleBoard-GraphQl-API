package com.source.bundleboard.refreshtoken.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("refresh_tokens")
public class RefreshToken {
    @Id
    Long id;

    @Column("users_id")
    Long userId;

    @Column("token")
    String token;

    @Column("issued_at")
    Instant issuedAt;

    @Column("expiration_time")
    Instant expirationTime;
}
