package com.source.bundleboard.auth.service;

import com.source.bundleboard.api.exception.*;
import com.source.bundleboard.auth.dto.AuthRequest;
import com.source.bundleboard.auth.dto.AuthResponse;
import com.source.bundleboard.auth.dto.RefreshTokenRequest;
import com.source.bundleboard.auth.dto.RegisterRequest;
import com.source.bundleboard.auth.jwt.JwtProperties;
import com.source.bundleboard.auth.jwt.service.JwtService;
import com.source.bundleboard.refreshtoken.model.RefreshToken;
import com.source.bundleboard.refreshtoken.repository.RefreshTokenRepository;
import com.source.bundleboard.user.model.User;
import com.source.bundleboard.user.model.UserRole;
import com.source.bundleboard.user.model.UserStatus;
import com.source.bundleboard.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    private final JwtProperties jwtProperties;


    @Transactional
    @Override
    public Mono<AuthResponse> authenticate(AuthRequest request) {
        return userRepository.findByUsername(request.username())
                .switchIfEmpty(Mono.error(new UserNotFoundException("User not found.")))
                .flatMap(user -> {

                    if (!passwordEncoder.matches(request.password(), user.passwordHash())) {
                        return Mono.error(new IncorrectPasswordException("Incorrect password."));
                    }

                    if (user.status() == UserStatus.banned) {
                        return Mono.error(new UserStatusException("User is banned."));
                    }

                    if (user.status() == UserStatus.inactive) {
                        return Mono.error(new UserStatusException("User is inactive."));
                    }

                    User updatedUser = new User(
                            user.id(), user.username(), user.email(), user.passwordHash(),
                            user.avatarUrl(), user.roles(), user.status(),
                            Instant.now(), user.createdAt()
                    );

                    return userRepository.save(updatedUser)
                            .flatMap(this::generateAuthResponse);
                });
    }

    @Transactional
    @Override
    public Mono<AuthResponse> register(RegisterRequest request) {
        return userRepository.existsByUsername(request.username())
                .flatMap(exists -> {
                    if (exists){
                        return Mono.error(new UserAlreadyExistsException("Username already exists."));
                    }
                    User user = new User(
                            null,
                            request.username(),
                            request.email(),
                            passwordEncoder.encode(request.password()),
                            "",
                            Set.of(UserRole.client),
                            UserStatus.active,
                            null,
                            Instant.now()
                    );
                    return userRepository.save(user);
                })
                .flatMap(this::generateAuthResponse);
    }

    @Override
    public Mono<AuthResponse> refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.refreshToken();
        return jwtService.isRefreshToken(refreshToken)
                .flatMap(isRefresh -> {
                    if (!isRefresh){
                        return Mono.error(new InvalidTokenException("Invalid refresh token."));
                    }
                    return refreshTokenRepository.existsByTokenAndExpirationTimeAfter(refreshToken, Instant.now());
                })
                .flatMap(exists -> {
                    if (!exists) return Mono.error(new InvalidTokenException("Token expired or revoked."));
                    return jwtService.extractUsername(refreshToken);
                })
                .flatMap(userRepository::findByUsername)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User not found.")))
                .flatMap(user -> {
                    if (user.status() == UserStatus.banned || user.status() == UserStatus.inactive) {
                        return Mono.error(new UserStatusException("User is banned or inactive."));
                    }

                    return refreshTokenRepository.deleteByToken(refreshToken)
                            .then(generateAuthResponse(user));
                });
    }

    @Override
    public Mono<Void> logout(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.refreshToken();
        refreshTokenRepository.deleteByToken(refreshToken);
        return Mono.empty();
    }

    private Mono<AuthResponse> generateAuthResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user.username(), UserRole.toAuthorities(user.roles()));
        String refreshToken = jwtService.generateRefreshToken(user.username());
        RefreshToken refreshTokenEntity = new RefreshToken(
                null,
                user.id(),
                refreshToken,
                Instant.now(),
                Instant.now().plusMillis(jwtProperties.getRefreshTokenExpirationMs())
        );

        return refreshTokenRepository.save(refreshTokenEntity)
                .map(savedRefreshToken -> new AuthResponse(accessToken, savedRefreshToken.token(), null));
    }
}
