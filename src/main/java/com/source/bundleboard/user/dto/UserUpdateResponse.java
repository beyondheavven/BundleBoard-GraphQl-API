package com.source.bundleboard.user.dto;


import java.time.Instant;

public record UserUpdateResponse(
        Long id,
        
        String username,
        
        String avatarUrl,

        Instant updatedAt,

        String accessToken,

        String refreshToken
) {
}
