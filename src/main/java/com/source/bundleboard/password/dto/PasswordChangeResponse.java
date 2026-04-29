package com.source.bundleboard.password.dto;

public record PasswordChangeResponse(
        Boolean success,
        String message,
        Integer attemptsLeft
) {
}
