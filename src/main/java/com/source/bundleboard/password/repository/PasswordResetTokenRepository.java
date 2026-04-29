package com.source.bundleboard.password.repository;

import com.source.bundleboard.password.model.PasswordResetToken;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends R2dbcRepository<PasswordResetToken, Long> {


}
