package com.source.bundleboard.user.dto;

import com.source.bundleboard.user.model.UserRole;
import com.source.bundleboard.user.model.UserStatus;

public record UserResponseDto(

        Long id,

        String username,

        String email,

        String avatarUrl,

        UserRole role,

        UserStatus status

) {
}
