package com.source.bundleboard.user.service;


import com.source.bundleboard.user.dto.UserResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<UserResponseDto> getUserById(Long id);

    Flux<UserResponseDto> getAllUsers();


}
