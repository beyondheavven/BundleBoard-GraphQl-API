package com.source.bundleboard.email.repository;

import com.source.bundleboard.email.model.EmailVerificationToken;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

@Repository
public interface EmailVerificationTokenRepository extends R2dbcRepository<EmailVerificationToken, Long> {

    Mono<EmailVerificationToken> findByToken(String token);

}
