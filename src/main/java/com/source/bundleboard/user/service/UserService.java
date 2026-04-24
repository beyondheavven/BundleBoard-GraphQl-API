package com.source.bundleboard.user.service;


import com.source.bundleboard.user.dto.UpdateUserRequest;
import com.source.bundleboard.user.dto.UserResponseDto;
import com.source.bundleboard.user.dto.UserUpdateResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<UserResponseDto> findUserByUsername(String username);

    Mono<UserResponseDto> findUserById(Long id);

    Flux<UserResponseDto> findAllUsers();


    Mono<UserUpdateResponse> updateMe(UpdateUserRequest request);

    Mono<UserResponseDto> findMe();
}
