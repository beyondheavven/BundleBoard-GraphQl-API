package com.source.bundleboard.collection.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateNewCollectionDto(

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Author ID is required")
        Long authorId,

        @NotNull(message = "Price is required")
        Double price,

        String videoTutorialUrl,

        @NotNull(message = "Media resource ID is required")
        Long mediaResourceId,

        @NotNull(message = "Preview image ID is required")
        Long previewImageId
) {
}
