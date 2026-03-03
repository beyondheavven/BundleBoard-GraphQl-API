package com.source.bundleboard.auth.service;

import com.source.bundleboard.auth.dto.AuthRequest;
import com.source.bundleboard.auth.dto.AuthResponse;
import com.source.bundleboard.auth.dto.RegisterRequest;
import com.source.bundleboard.auth.jwt.service.JwtService;
import com.source.bundleboard.user.model.User;
import com.source.bundleboard.user.model.UserRole;
import com.source.bundleboard.user.model.UserStatus;
import com.source.bundleboard.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;


    @Override
    public Mono<AuthResponse> authenticate(AuthRequest request) {
        return userRepository.findByUsername(request.username())
                .switchIfEmpty(Mono.error(new RuntimeException("User not found.")))
                .flatMap(user -> {

                    if (!passwordEncoder.matches(request.password(), user.passwordHash())) {
                        return Mono.error(new RuntimeException("Incorrect password."));
                    }

                    if (user.status() == UserStatus.banned) {
                        return Mono.error(new RuntimeException("User is banned."));
                    }

                    if (user.status() == UserStatus.inactive) {
                        return Mono.error(new RuntimeException("User is inactive."));
                    }

                    User updatedUser = new User(
                            user.id(), user.username(), user.email(), user.passwordHash(),
                            user.avatarUrl(), user.roles(), user.status(),
                            Instant.now(), user.createdAt()
                    );

                    return userRepository.save(updatedUser)
                            .map(savedUser -> {
                                String access = jwtService.generateAccessToken(
                                        savedUser.username(),
                                        UserRole.toAuthorities(savedUser.roles())
                                );
                                String refresh = jwtService.generateRefreshToken(savedUser.username());
                                return new AuthResponse(access, refresh, null);
                            });
                });
    }

    @Override
    public Mono<AuthResponse> register(RegisterRequest request) {
        return userRepository.existsByUsername(request.username())
                .flatMap(exists -> {
                    if (exists){
                        return Mono.error(new RuntimeException("Username already exists."));
                    }
                    User user = new User(
                            null,
                            request.username(),
                            request.email(),
                            passwordEncoder.encode(request.password()),
                            null,
                            Set.of(UserRole.client),
                            UserStatus.active,
                            null,
                            Instant.now()
                    );
                    return userRepository.save(user);
                })
                .map(user -> {
                    String access = jwtService.generateAccessToken(
                            user.username(),
                            UserRole.toAuthorities(user.roles())
                    );
                    String refresh = jwtService.generateRefreshToken(user.username());
                    return new AuthResponse(access, refresh, null);
                });
    }

    @Override
    public Mono<AuthResponse> refreshToken(String refreshToken) {
        return jwtService.isRefreshToken(refreshToken)
                .flatMap(isRefresh -> {
                    if (!isRefresh){
                        return Mono.error(new RuntimeException("Invalid refresh token."));
                    }
                    return jwtService.extractUsername(refreshToken);
                })
                .flatMap(userRepository::findByUsername)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found.")))
                .flatMap(user -> {
                    if (user.status() == UserStatus.banned) {
                        return Mono.error(new RuntimeException("User is banned."));
                    }

                    if (user.status() == UserStatus.inactive) {
                        return Mono.error(new RuntimeException("User is inactive."));
                    }

                    String newAccessToken = jwtService.generateAccessToken(
                            user.username(),
                            UserRole.toAuthorities(user.roles())
                    );

                    String newRefreshToken = jwtService.generateRefreshToken(user.username());
                    return Mono.just(new AuthResponse(newAccessToken, newRefreshToken, null));
                });
    }
}
