package com.source.bundleboard.user.dto;

public record UserUpdateResponse(
        Long id,
        
        String username,
        
        String email,

        String updatedAt
) {
}
