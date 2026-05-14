package com.source.bundleboard.refreshtoken.service;

import com.source.bundleboard.api.exception.RefreshTokenNotFoundException;
import com.source.bundleboard.refreshtoken.model.RefreshToken;
import com.source.bundleboard.refreshtoken.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;


    @Override
    public Mono<Void> deleteByUserId(Long id) {
        return refreshTokenRepository.deleteByUserId(id)
                .onErrorResume(e -> Mono.error(new RefreshTokenNotFoundException()));
    }

    @Override
    public Mono<RefreshToken> save(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken)
                .onErrorResume(e -> Mono.error(new RefreshTokenNotFoundException()));
    }
}
