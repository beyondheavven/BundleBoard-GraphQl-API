package com.source.bundleboard.auth.service;

import com.source.bundleboard.api.exception.*;
import com.source.bundleboard.auth.dto.AuthRequest;
import com.source.bundleboard.auth.dto.AuthResponse;
import com.source.bundleboard.auth.dto.RefreshTokenRequest;
import com.source.bundleboard.auth.dto.RegisterRequest;
import com.source.bundleboard.auth.jwt.JwtProperties;
import com.source.bundleboard.auth.jwt.service.JwtService;
import com.source.bundleboard.email.service.EmailVerificationTokenService;
import com.source.bundleboard.refreshtoken.model.RefreshToken;
import com.source.bundleboard.refreshtoken.repository.RefreshTokenRepository;
import com.source.bundleboard.user.dto.UserDto;
import com.source.bundleboard.user.model.User;
import com.source.bundleboard.user.model.UserRole;
import com.source.bundleboard.user.model.UserStatus;
import com.source.bundleboard.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

    private final EmailVerificationTokenService emailVerificationTokenService;


    @Transactional
    @Override
    public Mono<AuthResponse> authenticate(AuthRequest request) {
        return userRepository.findByUsername(request.username())
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .flatMap(user -> {

                    if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
                        return Mono.error(new IncorrectPasswordException("Incorrect password."));
                    }

                    if (user.getStatus() == UserStatus.banned) {
                        return Mono.error(new UserStatusException());
                    }

                    if (user.getStatus() == UserStatus.inactive) {
                        return Mono.error(new UserStatusException());
                    }

                    User updatedUser = new User(
                            user.getId(), user.getUsername(), user.getEmail(), user.getPasswordHash(),
                            user.getAvatarUrl(), user.getRoles(), user.getStatus(),
                            Instant.now(), user.getCreatedAt()
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
                        return Mono.error(new UserAlreadyExistsException());
                    }
                    User user = new User(
                            null,
                            request.username(),
                            request.email(),
                            passwordEncoder.encode(request.password()),
                            "",
                            Set.of(UserRole.client),
                            UserStatus.inactive,
                            null,
                            Instant.now()
                    );
                    return userRepository.save(user);
                })
                .flatMap(savedUser ->
                    emailVerificationTokenService.resendVerificationEmail(savedUser.getEmail())
                            .then(generateAuthResponse(savedUser))
                );
    }

    @Override
    public Mono<AuthResponse> refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.refreshToken();
        return jwtService.isRefreshToken(refreshToken)
                .flatMap(isRefresh -> {
                    if (!isRefresh){
                        return Mono.error(new InvalidTokenException());
                    }
                    return refreshTokenRepository.existsByTokenAndExpirationTimeAfter(refreshToken, Instant.now());
                })
                .flatMap(exists -> {
                    if (!exists) return Mono.error(new InvalidTokenException());
                    return jwtService.extractUsername(refreshToken);
                })
                .flatMap(userRepository::findByUsername)
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .flatMap(user -> {
                    if (user.getStatus() == UserStatus.banned || user.getStatus() == UserStatus.inactive) {
                        return Mono.error(new UserStatusException());
                    }

                    return refreshTokenRepository.deleteByToken(refreshToken)
                            .then(generateAuthResponse(user));
                });
    }

    @Override
    public Mono<Boolean> logout(RefreshTokenRequest refreshTokenRequest) {
        return refreshTokenRepository.deleteByToken(refreshTokenRequest.refreshToken())
                .thenReturn(true)
                .defaultIfEmpty(false);
    }

    private Mono<AuthResponse> generateAuthResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user.getUsername(), UserRole.toAuthorities(user.getRoles()));
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());

        UserDto userDto = new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.getRoles());

        RefreshToken refreshTokenEntity = new RefreshToken(
                null,
                user.getId(),
                refreshToken,
                Instant.now(),
                Instant.now().plusMillis(jwtProperties.getRefreshTokenExpirationMs())
        );

        return refreshTokenRepository.save(refreshTokenEntity)
                .map(savedRefreshToken -> new AuthResponse(accessToken, savedRefreshToken.getToken(), userDto, null));
    }
}
