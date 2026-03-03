package com.source.bundleboard.auth.dto;

public record AuthResponse(

        String accessToken,

        String refreshToken,

        String error
) {
}
