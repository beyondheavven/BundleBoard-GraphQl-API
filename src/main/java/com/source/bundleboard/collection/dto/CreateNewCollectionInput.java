package com.source.bundleboard.collection.dto;

import com.source.bundleboard.image.dto.ImageShortInput;
import com.source.bundleboard.mediaresource.dto.MediaResourceInput;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;

import java.math.BigDecimal;
import java.util.List;

public record CreateNewCollectionInput(
        @NotBlank(message = "asset title required")
        @Size(min = 3, max = 100, message = "asset title must be between 3 and 100 symbols")
        String name,

        @NotBlank(message = "description is required")
        @Size(min = 100, max = 1000, message = "description length must be between 100 and 1000 symbols")
        String description,

        @NotNull(message = "price required")
        @DecimalMin(value = "0.00", message = "price can not be negative")
        @Digits(integer = 6, fraction = 2, message = "invalid price format")
        BigDecimal price,

        String videoTutorialUrl,

        String externalLink,

        @NotEmpty(message = "at least one tag required")
        List<Long> tagIds,

        @Valid
        List<ImageShortInput> galleryImages,

        MediaResourceInput mediaResource
) {
}
