package com.source.bundleboard.payment.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PaymentRequest(

        @NotNull(message = "User ID is required")
        Long userId,

        @NotBlank(message = "Currency code cannot be empty")
        @Size(min = 3, max = 3, message = "Currency must be a 3-letter ISO code (e.g., USD)")
        String currency,

        @NotEmpty(message = "Collection IDs list cannot be empty")
        List<Long> collectionIds
) {
}
