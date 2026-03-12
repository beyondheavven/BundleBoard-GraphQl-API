package com.source.bundleboard.user.controller;

import com.source.bundleboard.user.dto.UserResponseDto;
import com.source.bundleboard.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @QueryMapping
    public Mono<UserResponseDto> me(){
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication())
                .filter(Authentication::isAuthenticated)
                .flatMap(auth -> {
                    String username = auth.getName();
                    return userService.findUserByUsername(username);
                });
    }

    @QueryMapping
    public Mono<UserResponseDto> getUserById(@Argument Long id){
        return userService.findUserById(id);
    }

    @QueryMapping
    public Flux<UserResponseDto> getAllUsers(){
        return userService.findAllUsers();
    }



}
