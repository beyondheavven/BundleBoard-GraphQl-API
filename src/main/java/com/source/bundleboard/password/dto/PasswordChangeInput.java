package com.source.bundleboard.password.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordChangeInput(

        @NotBlank(message = "Current password is required")
        String currentPassword,

        @NotBlank(message = "New password is required")
        @Size(min = 8, max = 30, message = "New password must be between 8 and 30 characters")
        String newPassword,

        @NotBlank(message = "New password confirmation is required")
        String newPasswordConfirmation
) {
}
