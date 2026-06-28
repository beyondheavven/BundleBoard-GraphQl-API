package com.source.bundleboard.refreshtoken.service;

import com.source.bundleboard.refreshtoken.model.RefreshToken;
import reactor.core.publisher.Mono;

public interface RefreshTokenService {

    Mono<Void> deleteByUserId(Long id);

    Mono<RefreshToken> save(RefreshToken refreshToken);

    Mono<Void> deleteAllExpired();
}
