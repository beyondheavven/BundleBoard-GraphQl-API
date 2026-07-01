package com.source.bundleboard.auth.service;

import com.source.bundleboard.api.exception.UserAlreadyExistsException;
import com.source.bundleboard.api.exception.UserNotFoundException;
import com.source.bundleboard.api.exception.IncorrectPasswordException;
import com.source.bundleboard.api.exception.InvalidTokenException;
import com.source.bundleboard.api.exception.UserStatusException;
import com.source.bundleboard.auth.dto.*;
import com.source.bundleboard.auth.jwt.JwtProperties;
import com.source.bundleboard.auth.jwt.service.JwtService;
import com.source.bundleboard.email.service.EmailVerificationTokenService;
import com.source.bundleboard.refreshtoken.model.RefreshToken;
import com.source.bundleboard.refreshtoken.repository.RefreshTokenRepository;
import com.source.bundleboard.user.dto.UserDto;
import com.source.bundleboard.user.model.User;
import com.source.bundleboard.user.model.UserRole;
import com.source.bundleboard.user.model.UserStatus;
import com.source.bundleboard.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;

    private final RefreshTokenRepository refreshTokenRepository;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    private final JwtProperties jwtProperties;

    private final EmailVerificationTokenService emailVerificationTokenService;


    @Override
    @Transactional
    public Mono<AuthResponse> authenticate(AuthRequest request) {
        log.info("🟢 Attempting to authenticate user by identifier: {}", request.identifier());
        return userService.findByIdentifier(request.identifier())
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Authentication failed: User not found [{}]", request.identifier());
                    return Mono.error(new UserNotFoundException());
                }))
                .flatMap(user -> {

                    if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
                        log.warn("Authentication failed: Incorrect password for user [{}]", request.identifier());
                        return Mono.error(new IncorrectPasswordException("Incorrect password."));
                    }

                    if (user.getStatus() == UserStatus.banned || user.getStatus() == UserStatus.inactive) {
                        log.warn("Authentication failed: Status is {} for user [{}]", user.getStatus(), request.identifier());
                        return Mono.error(new UserStatusException());
                    }

                    User updatedUser = new User(
                            user.getId(), user.getUsername(), user.getEmail(), user.getPasswordHash(),
                            user.getAvatarUrl(), user.getRoles(), user.getStatus(),
                            Instant.now(), user.getCreatedAt(), user.isSetupCompleted()
                    );

                    return userService.save(updatedUser)
                            .flatMap(savedUser -> generateAuthResponse(savedUser, !savedUser.isSetupCompleted()));
                })
                .doOnSuccess(response -> log.info("🟢 User authenticated successfully: {}", request.identifier()))
                .doOnError(e -> log.error("🔴 Error during authentication for user [{}]: {}", request.identifier(), e.getMessage()));
    }

    @Override
    @Transactional
    public Mono<AuthResponse> register(RegisterRequest request) {
        log.info("🟢 Attempting to register new user: {}", request.username());
        return userService.existsByUsername(request.username())
                .flatMap(usernameExists -> {
                    if (usernameExists){
                        log.warn("🟡 Registration failed: Username already exists [{}]", request.username());
                        return Mono.error(new UserAlreadyExistsException());
                    }
                    return userService.getUserByEmail(request.email())
                            .flatMap(existingUser -> {
                                log.warn("🟡 Registration failed: Email already exists [{}]", request.email());
                                return Mono.<User>error(new UserAlreadyExistsException());
                            })
                            .switchIfEmpty(Mono.defer(() -> {
                                User user = new User(
                                        null,
                                        request.username(),
                                        request.email(),
                                        passwordEncoder.encode(request.password()),
                                        "",
                                        Set.of(UserRole.client),
                                        UserStatus.inactive,
                                        null,
                                        Instant.now(),
                                        true
                                );
                                return userService.save(user);
                            }));
                })
                .flatMap(savedUser ->
                        emailVerificationTokenService.resendVerificationEmail(savedUser.getEmail())
                                .doOnSuccess(v -> log.info("🟢 Verification email sent to: {}", savedUser.getEmail()))
                                .then(generateAuthResponse(savedUser, false))
                )
                .doOnSuccess(response -> log.info("🟢 User registered successfully: {}", request.username()))
                .doOnError(e -> log.error("🔴 Error during registration for user [{}]: {}", request.username(), e.getMessage()))
                .onErrorResume(e -> Mono.just(new AuthResponse(null, null, null, e.getMessage(), false)));
    }

    @Override
    @Transactional
    public Mono<RefreshResponse> refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.refreshToken();
        log.info("🟢 Attempting to refresh token");
        return jwtService.isRefreshToken(refreshToken)
                .flatMap(isRefresh -> {
                    if (!isRefresh){
                        log.warn("🟡 Token refresh failed: Provided token is not a refresh token");
                        return Mono.error(new InvalidTokenException());
                    }
                    return refreshTokenRepository.existsByTokenAndExpirationTimeAfter(refreshToken, Instant.now());
                })
                .flatMap(exists -> {
                    if (!exists) {
                        log.warn("🟡 Token refresh failed: Token does not exist or is expired in DB");
                        return Mono.error(new InvalidTokenException());
                    }
                    return jwtService.validateToken(refreshToken);
                }).flatMap(claims -> {
                    Number userIdNumber = claims.get("userId", Number.class);

                    if (userIdNumber != null) {
                        return userService.getUserById(userIdNumber.longValue());
                    }

                    String username = claims.getSubject();
                    if (username != null) {
                        log.info("ℹ️ Legacy refresh token detected without userId. Falling back to username lookup: [{}]", username);
                        return userService.getUserByUsername(username);
                    }
                log.warn("🔴 Token refresh failed: Token claims are corrupted or missing identifier targets");
                return Mono.error(new InvalidTokenException());
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("🟡 Token refresh failed: User node no longer exists in database");
                    return Mono.error(new UserNotFoundException());
                }))
                .flatMap(user -> {
                    if (user.getStatus() == UserStatus.banned || user.getStatus() == UserStatus.inactive) {
                        log.warn("🟡 Token refresh failed: User status is {} for user [{}]", user.getStatus(), user.getUsername());
                        return Mono.error(new UserStatusException());
                    }

                    return refreshTokenRepository.deleteByToken(refreshToken)
                            .then(generateRefreshResponse(user));
                })
                .doOnSuccess(response -> log.info("🟢 Token refreshed successfully"))
                .doOnError(e -> log.error("🔴 Error during token refresh: {}", e.getMessage()));
    }

    @Override
    @Transactional
    public Mono<Boolean> logout(RefreshTokenRequest refreshTokenRequest) {
        log.info("🟢 Attempting logout (invalidating token)");
        return refreshTokenRepository.deleteByToken(refreshTokenRequest.refreshToken())
                .thenReturn(true)
                .defaultIfEmpty(false)
                .doOnSuccess(success -> {
                    if (success) {
                        log.info("🟢 Logout successful: Token invalidated");
                    } else {
                        log.warn("🟡 Logout warning: Token not found in DB");
                    }
                })
                .doOnError(e -> log.error("🔴 Error during logout: {}", e.getMessage()));
    }

    @Override
    @Transactional
    public Mono<AuthResponse> socialLogin(SocialLoginRequest input) {
        log.info("🟢 Attempting social login for email: {}", input.email());
        return userService.getUserByEmail(input.email())
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("🟡 Social login failed: User not found [{}]", input.email());
                    return Mono.error(new UserNotFoundException());
                }))
                .flatMap(user -> {
                    if (user.getStatus() == UserStatus.banned || user.getStatus() == UserStatus.inactive) {
                        log.warn("🟡 Social login failed: Status is {} for user [{}]", user.getStatus(), user.getEmail());
                        return Mono.error(new UserStatusException());
                    }

                    User updatedUser = new User(
                            user.getId(), user.getUsername(), user.getEmail(), user.getPasswordHash(),
                            user.getAvatarUrl(), user.getRoles(), user.getStatus(),
                            Instant.now(), user.getCreatedAt(), user.isSetupCompleted()
                    );

                    return userService.save(updatedUser)
                            .flatMap(savedUser -> generateAuthResponse(savedUser, false));
                })
                .doOnSuccess(response -> log.info("🟢 Social login successful for: {}", input.email()))
                .doOnError(e -> log.error("🔴 Error during social login for [{}]: {}", input.email(), e.getMessage()));
    }

    @Override
    @Transactional
    public Mono<AuthResponse> socialRegister(SocialRegisterRequest input) {
        log.info("🟢 Attempting social registration for email: {}", input.email());
        return userService.getUserByEmail(input.email())
                .flatMap(existingUser -> {
                    log.warn("🟡 Social registration failed: Email already exists [{}]", input.email());
                    return Mono.<String>error(new UserAlreadyExistsException());
                })
                .onErrorResume(UserNotFoundException.class, e -> Mono.empty())
                .then(makeUsernameUnique(input.username()))
                .flatMap(uniqueUsername -> {
                    User user = new User(
                            null,
                            uniqueUsername,
                            input.email(),
                            passwordEncoder.encode(input.password()),
                            "",
                            Set.of(UserRole.client),
                            UserStatus.active,
                            null,
                            Instant.now(),
                            true
                    );
                    return userService.save(user);
                })
                .flatMap(savedUser -> generateAuthResponse(savedUser, true))
                .doOnSuccess(response -> log.info("🟢 Social registration successful for: {}", input.email()))
                .doOnError(e -> log.error("🔴 Error during social registration for [{}]: {}", input.email(), e.getMessage()));
    }

    private Mono<AuthResponse> generateAuthResponse(User user, boolean isNew) {
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getUsername(), UserRole.toAuthorities(user.getRoles()));
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getUsername());

        UserDto userDto = new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.getRoles());

        RefreshToken refreshTokenEntity = new RefreshToken(
                null,
                user.getId(),
                refreshToken,
                Instant.now(),
                Instant.now().plusMillis(jwtProperties.getRefreshTokenExpirationMs())
        );

        return refreshTokenRepository.save(refreshTokenEntity)
                .map(savedRefreshToken -> new AuthResponse(accessToken, savedRefreshToken.getToken(), userDto, null, isNew));
    }

    private Mono<RefreshResponse> generateRefreshResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getUsername(), UserRole.toAuthorities(user.getRoles()));
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getUsername());

        RefreshToken refreshTokenEntity = new RefreshToken(
                null,
                user.getId(),
                refreshToken,
                Instant.now(),
                Instant.now().plusMillis(jwtProperties.getRefreshTokenExpirationMs())
        );

        return refreshTokenRepository.save(refreshTokenEntity)
                .map(saved -> new RefreshResponse(accessToken, saved.getToken()));
    }

    private Mono<String> makeUsernameUnique(String username) {
        return userService.existsByUsername(username)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.just(username);
                    }
                    String suffix = "_" + java.util.UUID.randomUUID().toString().substring(0, 4);
                    String newUsername = username + suffix;
                    return makeUsernameUnique(newUsername);
                });
    }
}
