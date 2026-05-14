package com.source.bundleboard.user.controller;

import com.source.bundleboard.user.dto.UserResponseDto;
import com.source.bundleboard.user.dto.UserUpdateResponse;
import com.source.bundleboard.user.dto.UserProfileResponse;
import com.source.bundleboard.user.dto.UpdateUserRequest;
import com.source.bundleboard.user.dto.UpdateUserRoleInput;
import com.source.bundleboard.user.dto.UpdateUserRoleResponse;
import com.source.bundleboard.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @QueryMapping
    @PreAuthorize("hasAnyRole('CLIENT','AUTHOR','ADMIN')")
    public Mono<UserResponseDto> me(){
        return userService.findMe();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Mono<UserResponseDto> getUserById(@Argument Long id){
        return userService.findUserById(id);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Flux<UserResponseDto> getAllUsers(){
        return userService.findAllUsers();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('CLIENT', 'AUTHOR', 'ADMIN')")
    public Mono<UserUpdateResponse> updateMe(@Argument UpdateUserRequest input) {
        return userService.updateMe(input);
    }

    @MutationMapping
    @PreAuthorize("permitAll()")
    public Mono<UpdateUserRoleResponse> updateUserRole(@Argument UpdateUserRoleInput input) {
        return userService.updateUserRole(input);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('CLIENT', 'AUTHOR')")
    public Mono<UserProfileResponse> getUserProfile(){
        return userService.getUserProfile();
    }

    @QueryMapping
    @PreAuthorize("permitAll()")
    public Mono<String> debugSecurity() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> {
                    var auth = ctx.getAuthentication();
                    if (auth == null) return "Security Context is EMPTY";

                    return String.format(
                            "User: %s, Authenticated: %b, Authorities: %s",
                            auth.getName(),
                            auth.isAuthenticated(),
                            auth.getAuthorities()
                    );
                })
                .defaultIfEmpty("No Security Context found");
    }


}
