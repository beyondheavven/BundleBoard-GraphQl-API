package com.source.bundleboard.user.service;

import com.source.bundleboard.api.exception.UserNotFoundException;
import com.source.bundleboard.purchase.service.PurchaseService;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final PurchaseService purchaseService;

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
    public Mono<UpdateUserRoleResponse> updateUserRole(UpdateUserRoleInput input) {
        return userRepository.findByEmail(input.email())
                .flatMap(user -> {
                    if (input.role() != null){
                        try{
                            UserRole newRole = UserRole.valueOf(input.role());

                            Set<UserRole> currentRoles = user.getRoles();

                            currentRoles.add(newRole);

                            user.setRoles(currentRoles);
                        }catch (IllegalArgumentException e){
                            return Mono.error(new IllegalArgumentException("Invalid role"));
                        }
                    }
                    return userRepository.save(user);
                })
                .map(savedUser -> new UpdateUserRoleResponse("User role update successfully", true))
                .switchIfEmpty(Mono.error(new UserNotFoundException()));

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
    public Mono<UserProfileResponse> getUserProfile(String email) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .flatMap(user -> purchaseService.findAllByClientId(user.getId())
                        .flatMap(purchase -> purchaseService.fetch(purchase))
                        .coll);
    }


}
