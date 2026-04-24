package com.source.bundleboard.user.dto;

import jakarta.annotation.Nullable;

public record UpdateUserRequest(

        @Nullable
        String username,

        @Nullable
        String avatarUrl
) {
}
