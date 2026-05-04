package com.source.bundleboard.user.controller;

import com.source.bundleboard.user.dto.*;
import com.source.bundleboard.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @QueryMapping
    public Mono<UserResponseDto> me(){
        return userService.findMe();
    }

    @QueryMapping
    public Mono<UserResponseDto> getUserById(@Argument Long id){
        return userService.findUserById(id);
    }

    @QueryMapping
    public Flux<UserResponseDto> getAllUsers(){
        return userService.findAllUsers();
    }

    @MutationMapping
    public Mono<UserUpdateResponse> updateMe(@Argument UpdateUserRequest input) {
        return userService.updateMe(input);
    }

    @MutationMapping
    public Mono<UpdateUserRoleResponse> updateUserRole(@Argument UpdateUserRoleInput input) {
        return userService.updateUserRole(input);
    }





}
