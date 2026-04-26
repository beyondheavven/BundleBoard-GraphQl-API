package com.source.bundleboard.user.service;


import com.source.bundleboard.user.dto.UpdateUserRequest;
import com.source.bundleboard.user.dto.UserResponseDto;
import com.source.bundleboard.user.dto.UserUpdateResponse;
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

    Mono<User> saveUser(User user);

    Mono<User> getUserByEmail(String email);

    Mono<User> getUserByUsername(String username);
}
