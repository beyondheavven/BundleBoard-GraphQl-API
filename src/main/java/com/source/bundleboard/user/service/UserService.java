package com.source.bundleboard.user.service;


import com.source.bundleboard.user.dto.*;
import com.source.bundleboard.user.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<UserResponseDto> findUserByUsername(String username);

    Mono<UserResponseDto> findUserById(Long id);

    Flux<UserResponseDto> findAllUsers();

    Mono<UserUpdateResponse> updateMe(UpdateUserRequest request);

    Mono<UserResponseDto> findMe();

    Mono<User> getUserById(Long id);

    Mono<User> getUserByEmail(String email);

    Mono<User> getUserByUsername(String username);

    Mono<UpdateUserRoleResponse> updateUserRole(UpdateUserRoleInput input);

    Mono<User> findByUsername(String username);

    Mono<User> save(User user);

    Mono<Boolean> existsByUsername(String username);
}
