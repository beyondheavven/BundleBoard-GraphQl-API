package com.source.bundleboard.refreshtoken;

import com.source.bundleboard.api.exception.RefreshTokenNotFoundException;
import com.source.bundleboard.refreshtoken.model.RefreshToken;
import com.source.bundleboard.refreshtoken.repository.RefreshTokenRepository;
import com.source.bundleboard.refreshtoken.service.RefreshTokenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    private RefreshToken sampleToken;

    @BeforeEach
    void setUp() {
        sampleToken = new RefreshToken(
                1L,
                100L,
                "mock-refresh-token-xyz",
                Instant.now(),
                Instant.now().plus(1, ChronoUnit.DAYS)
        );
    }


    @Test
    void deleteByUserId_Success() {
        Long userId = 100L;
        when(refreshTokenRepository.deleteByUserId(userId)).thenReturn(Mono.empty());

        Mono<Void> result = refreshTokenService.deleteByUserId(userId);

        StepVerifier.create(result)
                .verifyComplete();

        verify(refreshTokenRepository, times(1)).deleteByUserId(userId);
    }

    @Test
    void deleteByUserId_RepositoryThrowsError_MapsToRefreshTokenNotFoundException() {
        Long userId = 100L;
        when(refreshTokenRepository.deleteByUserId(userId))
                .thenReturn(Mono.error(new RuntimeException("Database down")));

        Mono<Void> result = refreshTokenService.deleteByUserId(userId);

        StepVerifier.create(result)
                .expectError(RefreshTokenNotFoundException.class)
                .verify();

        verify(refreshTokenRepository, times(1)).deleteByUserId(userId);
    }


    @Test
    void save_Success() {
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(Mono.just(sampleToken));

        Mono<RefreshToken> result = refreshTokenService.save(sampleToken);

        StepVerifier.create(result)
                .assertNext(savedToken -> {
                    org.junit.jupiter.api.Assertions.assertNotNull(savedToken.getId());
                    org.junit.jupiter.api.Assertions.assertEquals(100L, savedToken.getUserId());
                    org.junit.jupiter.api.Assertions.assertEquals("mock-refresh-token-xyz", savedToken.getToken());
                })
                .verifyComplete();

        verify(refreshTokenRepository, times(1)).save(sampleToken);
    }

    @Test
    void save_RepositoryThrowsError_MapsToRefreshTokenNotFoundException() {
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenReturn(Mono.error(new RuntimeException("Unique constraint violation")));

        Mono<RefreshToken> result = refreshTokenService.save(sampleToken);

        StepVerifier.create(result)
                .expectError(RefreshTokenNotFoundException.class)
                .verify();

        verify(refreshTokenRepository, times(1)).save(sampleToken);
    }
}