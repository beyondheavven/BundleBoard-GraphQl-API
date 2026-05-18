package com.source.bundleboard.user.service;

import com.source.bundleboard.api.exception.UserNotFoundException;
import com.source.bundleboard.auth.jwt.JwtProperties;
import com.source.bundleboard.auth.jwt.service.JwtService;
import com.source.bundleboard.author.model.Author;
import com.source.bundleboard.author.repository.AuthorRepository;
import com.source.bundleboard.author.service.AuthorService;
import com.source.bundleboard.client.service.ClientService;
import com.source.bundleboard.purchase.service.PurchaseService;
import com.source.bundleboard.refreshtoken.model.RefreshToken;
import com.source.bundleboard.refreshtoken.service.RefreshTokenService;
import com.source.bundleboard.user.dto.*;
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


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final PurchaseService purchaseService;

    private final ClientService clientService;

    private final AuthorRepository authorRepository;

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
    @Transactional
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
                .flatMap(user -> assignRole(user, input.role()))
                .flatMap(userRepository::save)
                .flatMap(this::initializeRoleProfile)
                .flatMap(this::generateTokensAndResponse);
    }

    private Mono<User> assignRole(User user, String roleStr) {
        if (roleStr == null) return Mono.just(user);
        try {
            UserRole newRole = UserRole.valueOf(roleStr.toLowerCase());
            if (!user.getRoles().contains(newRole)) {
                user.getRoles().add(newRole);
            }
            return Mono.just(user);
        } catch (IllegalArgumentException e) {
            return Mono.error(new IllegalArgumentException("Invalid role: " + roleStr));
        }
    }

    private Mono<User> initializeRoleProfile(User user) {
        boolean isClient = user.getRoles().contains(UserRole.client);
        boolean isAuthor = user.getRoles().contains(UserRole.author);

        Mono<Void> initData = Mono.empty();
        if (isClient) {
            initData = clientService.createClientByUserId(user.getId()).then();
        } else if (isAuthor) {
            initData = authorRepository.findByUserId(user.getId())
                    .flatMap(existing -> Mono.empty())
                    .switchIfEmpty(Mono.defer(() -> {
                        Author newAuthor = new Author();
                        newAuthor.setUserId(user.getId());
                        return authorRepository.save(newAuthor);
                    })).then();
        }

        return initData.then(Mono.just(user));
    }

    private Mono<UpdateUserRoleResponse> generateTokensAndResponse(User user) {
        String newAccessToken = jwtService.generateAccessToken(
                user.getUsername(),
                UserRole.toAuthorities(user.getRoles())
        );
        String newRefreshTokenString = jwtService.generateRefreshToken(user.getUsername());

        return refreshTokenService.deleteByUserId(user.getId())
                .then(refreshTokenService.save(new RefreshToken(
                        null,
                        user.getId(),
                        newRefreshTokenString,
                        Instant.now(),
                        Instant.now().plusMillis(jwtProperties.getRefreshTokenExpirationMs())
                )))
                .map(savedToken -> new UpdateUserRoleResponse(
                        "User role updated and profile initialized successfully",
                        true,
                        newAccessToken,
                        savedToken.getToken()
                ));
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

    @Override
    @Transactional
    public Mono<UpdateAvatarResponse> updateUserAvatar(UpdateAvatarRequest input) {
        return userRepository.findById(input.id())
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .flatMap(user -> {
                    user.setAvatarUrl(input.avatarUrl());
                    return userRepository.save(user);
                })
                .map(updatedUser -> new UpdateAvatarResponse(
                        updatedUser.getId(),
                        updatedUser.getAvatarUrl(),
                        Instant.now()
                ));
    }


}
