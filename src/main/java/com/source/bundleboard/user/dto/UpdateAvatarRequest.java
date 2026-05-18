package com.source.bundleboard.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateAvatarRequest(

        @NotNull(message = "User ID is required")
        Long id,

        @NotBlank(message = "Avatar URL is required")
        String avatarUrl
) {
}
