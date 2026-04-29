package com.source.bundleboard.password.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PasswordResetInput(

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email
) {
}
