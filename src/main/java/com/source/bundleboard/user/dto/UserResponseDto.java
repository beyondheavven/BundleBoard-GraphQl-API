package com.source.bundleboard.user.dto;

import com.source.bundleboard.user.model.UserRole;
import com.source.bundleboard.user.model.UserStatus;

import java.util.Set;

public record UserResponseDto(

        Long id,

        String username,

        String email,

        String avatarUrl,

        Set<UserRole> roles,

        UserStatus status

) {
}
