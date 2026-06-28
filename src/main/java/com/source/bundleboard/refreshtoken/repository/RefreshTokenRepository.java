package com.source.bundleboard.refreshtoken.repository;

import com.source.bundleboard.refreshtoken.model.RefreshToken;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Repository
public interface RefreshTokenRepository extends R2dbcRepository<RefreshToken, Long> {

    Mono<Void> deleteByToken(String token);

    Mono<Boolean> existsByTokenAndExpirationTimeAfter(String token, Instant now);

    Mono<Void> deleteByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM refresh_token WHERE expiration_time < :now")
    Mono<Integer> deleteExpiredTokens(@Param("now")Instant now);
}
