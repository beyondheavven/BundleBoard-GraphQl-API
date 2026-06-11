package com.source.bundleboard.user.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRequest(

        @NotNull
        Long id,

        @Nullable
        String username,

        @Nullable
        String avatarUrl
) {
}
