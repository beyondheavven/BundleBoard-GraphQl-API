package com.source.bundleboard.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartItem(

        @NotNull(message = "Collection ID is required")
        Long id,

        @NotBlank(message = "Collection name cannot be empty")
        String name,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be greater than zero")
        Double price
) {
}
