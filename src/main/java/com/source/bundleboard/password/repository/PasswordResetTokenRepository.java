package com.source.bundleboard.password.repository;

import com.source.bundleboard.password.model.PasswordResetToken;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PasswordResetTokenRepository extends R2dbcRepository<PasswordResetToken, Long> {


    Mono<PasswordResetToken> findByUserIdAndCode(Long id, String hashedCode);

    Mono<PasswordResetToken> findByCode(String hashedToken);
}
