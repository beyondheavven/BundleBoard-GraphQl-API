package com.source.bundleboard.collection.dto;

import com.source.bundleboard.image.model.PreviewImage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.util.List;

public record UpdateCollectionRequest(

        @NotBlank(message = "asset title required")
        String name,

        @NotBlank(message = "description is required")
        String description,

        @NotNull(message = "price required")
        @Min(value = 0, message = "price can not be negative")
        BigDecimal price,

        @Valid
        List<PreviewImage> galleryImages,

        @Pattern(regexp = "^(https?://.*)?$", message = "invalid external link format")
        String externalLink

) {
}
