package com.source.bundleboard.auth.dto;

public record RefreshResponse(
        String accessToken,

        String refreshToken
) {
}
