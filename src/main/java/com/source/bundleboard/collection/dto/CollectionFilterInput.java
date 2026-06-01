package com.source.bundleboard.collection.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record CollectionFilterInput(

        @NotBlank(message = "Tag name is required")
        String tagName,

        @PositiveOrZero(message = "Page number cannot be negative")
        int page,

        @Min(value = 1, message = "Page size must be at least 1")
        int size
) {
}
