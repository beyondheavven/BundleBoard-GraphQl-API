package com.source.bundleboard.password.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordConfirmResetInput(

        @NotBlank
        String token,

        @NotBlank
        @Size(min = 8, max = 30, message = "New password must be between 8 and 30 characters")
        String newPassword,

        @NotBlank
        @Size(min = 8, max = 30, message = "New password must be between 8 and 30 characters")
        String confirmPassword

) {
}
