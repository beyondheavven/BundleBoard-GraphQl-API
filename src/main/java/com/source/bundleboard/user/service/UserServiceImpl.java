package com.source.bundleboard.user.service;

import com.source.bundleboard.api.exception.UserNotFoundException;
import com.source.bundleboard.auth.jwt.JwtProperties;
import com.source.bundleboard.auth.jwt.service.JwtService;
import com.source.bundleboard.client.service.ClientService;
import com.source.bundleboard.purchase.service.PurchaseService;
import com.source.bundleboard.refreshtoken.model.RefreshToken;
import com.source.bundleboard.refreshtoken.repository.RefreshTokenRepository;
import com.source.bundleboard.refreshtoken.service.RefreshTokenService;
import com.source.bundleboard.user.dto.UserResponseDto;
import com.source.bundleboard.user.dto.UserUpdateResponse;
import com.source.bundleboard.user.dto.UserProfileResponse;
import com.source.bundleboard.user.dto.UpdateUserRequest;
import com.source.bundleboard.user.dto.UpdateUserRoleInput;
import com.source.bundleboard.user.dto.UpdateUserRoleResponse;
import com.source.bundleboard.user.mapper.UserMapper;
import com.source.bundleboard.user.model.User;
import com.source.bundleboard.user.model.UserRole;
import com.source.bundleboard.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final PurchaseService purchaseService;

    private final ClientService clientService;

    private final JwtService jwtService;

    private final RefreshTokenService refreshTokenService;

    private final JwtProperties jwtProperties;

    @Override
    public Mono<UserResponseDto> findUserByUsername(String username) {
        return userRepository.findByUsername(username).map(userMapper::toDto).switchIfEmpty(Mono.error(new UserNotFoundException()));
    }

    @Override
    public Mono<UserResponseDto> findUserById(Long id) {
        return userRepository.findById(id).map(userMapper::toDto)
                .switchIfEmpty(Mono.error(new UserNotFoundException()));
    }

    @Override
    public Flux<UserResponseDto> findAllUsers() {
        return userRepository.findAll().map(userMapper::toDto)
                .switchIfEmpty(Flux.error(new UserNotFoundException()));
    }

    @Override
    public Mono<UserUpdateResponse> updateMe(UpdateUserRequest request) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication())
                .filter(Authentication::isAuthenticated)
                .flatMap(authentication -> userRepository.findByUsername(authentication.getName()))
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .flatMap( user -> {

                    if (request.avatarUrl() != null){
                        user.setAvatarUrl(request.avatarUrl());
                    }

                    if(request.username() != null){
                        user.setUsername(request.username());
                    }

                    return userRepository.save(user);
                })
                .map(userMapper::toUpdateResponse);
    }

    @Override
    public Mono<UserResponseDto> findMe() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .flatMap(authentication -> userRepository.findByUsername(authentication.getName()))
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .map(userMapper::toDto);
    }

    @Override
    public Mono<User> getUserById(Long id) {
        return userRepository.findById(id).switchIfEmpty(Mono.error(new UserNotFoundException()));
    }

    @Override
    public Mono<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email).switchIfEmpty(Mono.error(new UserNotFoundException()));
    }

    @Override
    public Mono<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username).switchIfEmpty(Mono.error(new UserNotFoundException()));
    }

    @Override
    @Transactional
    public Mono<UpdateUserRoleResponse> updateUserRole(UpdateUserRoleInput input) {
        return userRepository.findByEmail(input.email())
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .flatMap(user -> {
                    if (input.role() != null) {
                        try {
                            UserRole newRole = UserRole.valueOf(input.role().toLowerCase());
                            user.getRoles().add(newRole);
                        } catch (IllegalArgumentException e) {
                            return Mono.error(new IllegalArgumentException("Invalid role: " + input.role()));
                        }
                    }
                    return userRepository.save(user);
                })
                .flatMap(savedUser -> {
                    String newAccessToken = jwtService.generateAccessToken(
                            savedUser.getUsername(),
                            UserRole.toAuthorities(savedUser.getRoles())
                    );
                    String newRefreshTokenString = jwtService.generateRefreshToken(savedUser.getUsername());

                    return refreshTokenService.deleteByUserId(savedUser.getId())
                            .then(refreshTokenService.save(new RefreshToken(
                                    null,
                                    savedUser.getId(),
                                    newRefreshTokenString,
                                    Instant.now(),
                                    Instant.now().plusMillis(jwtProperties.getRefreshTokenExpirationMs())
                            )))
                            .map(savedRefreshToken -> new UpdateUserRoleResponse(
                                    "User role updated successfully",
                                    true,
                                    newAccessToken,
                                    savedRefreshToken.getToken()
                            ));
                });
    }

    @Override
    public Mono<User> findByUsername(String username) {
        return userRepository.findByUsername(username).switchIfEmpty(Mono.error(new UserNotFoundException()));
    }

    @Override
    public Mono<User> save(User user) {
        return userRepository.save(user).switchIfEmpty(Mono.error(new UserNotFoundException()));
    }

    @Override
    public Mono<Boolean> existsByUsername(String username) {
        return userRepository.existsByUsername(username).switchIfEmpty(Mono.error(new UserNotFoundException()));
    }

    @Override
    public Mono<UserProfileResponse> getUserProfile() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .flatMap(auth -> userRepository.findByUsername(auth.getName()))
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .flatMap(user ->
                        clientService.findByUserId(user.getId())
                                .flatMap(client ->
                                        purchaseService.findAllByClientId(client.getId())
                                                .defaultIfEmpty(Collections.emptyList()
                                                )
                                ).defaultIfEmpty(Collections.emptyList())
                                .map(purchase -> new UserProfileResponse(
                                        user.getId(),
                                        user.getUsername(),
                                        user.getEmail(),
                                        user.getAvatarUrl(),
                                        user.getStatus(),
                                        purchase
                                ))
                );
    }


}
