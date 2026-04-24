package com.source.bundleboard.user.service;

import com.source.bundleboard.api.exception.UserNotFoundException;
import com.source.bundleboard.user.dto.UpdateUserRequest;
import com.source.bundleboard.user.dto.UserResponseDto;
import com.source.bundleboard.user.dto.UserUpdateResponse;
import com.source.bundleboard.user.mapper.UserMapper;
import com.source.bundleboard.user.model.User;
import com.source.bundleboard.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

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


}
