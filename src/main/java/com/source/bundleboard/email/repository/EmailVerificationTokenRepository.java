package com.source.bundleboard.email.repository;

import com.source.bundleboard.email.model.EmailVerificationToken;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailVerificationTokenRepository extends R2dbcRepository<EmailVerificationToken, Long> {
}
