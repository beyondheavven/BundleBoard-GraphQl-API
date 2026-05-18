package com.source.bundleboard.user.dto;

import java.time.Instant;

public record UpdateAvatarResponse(

        Long id,

        String avatarUrl,

        Instant updatedAt
) {
}
