package com.source.bundleboard.password.dto;

public record PasswordResetResponse(
        Boolean success,
        String message,
        String token
) {
}
