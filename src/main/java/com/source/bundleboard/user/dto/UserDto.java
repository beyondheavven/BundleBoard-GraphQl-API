package com.source.bundleboard.user.dto;

import com.source.bundleboard.user.model.UserRole;

import java.util.Set;

public record UserDto(
        Long id,

        String username,

        String email,

        Set<UserRole> roles
) {
}
