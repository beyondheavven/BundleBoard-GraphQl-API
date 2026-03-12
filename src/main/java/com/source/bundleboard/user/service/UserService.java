package com.source.bundleboard.user.service;


import com.source.bundleboard.user.dto.UserResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<UserResponseDto> findUserByUsername(String username);

    Mono<UserResponseDto> findUserById(Long id);

    Flux<UserResponseDto> findAllUsers();


}
