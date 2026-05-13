package com.source.bundleboard.user.controller;

import com.source.bundleboard.user.dto.*;
import com.source.bundleboard.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @QueryMapping
    @PreAuthorize( "hasAnyRole('user','author', 'admin')")
    public Mono<UserResponseDto> me(){
        return userService.findMe();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('admin')")
    public Mono<UserResponseDto> getUserById(@Argument Long id){
        return userService.findUserById(id);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('admin')")
    public Flux<UserResponseDto> getAllUsers(){
        return userService.findAllUsers();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('client', 'author', 'admin')")
    public Mono<UserUpdateResponse> updateMe(@Argument UpdateUserRequest input) {
        return userService.updateMe(input);
    }

    @MutationMapping
    @PreAuthorize("permitAll()")
    public Mono<UpdateUserRoleResponse> updateUserRole(@Argument UpdateUserRoleInput input) {
        return userService.updateUserRole(input);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('client, author')")
    public Mono<UserProfileResponse> getUserProfile(@Argument String email){
        return userService.getUserProfile(email);
    }


}
