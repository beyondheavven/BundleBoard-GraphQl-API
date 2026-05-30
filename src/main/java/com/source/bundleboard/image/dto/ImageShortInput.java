package com.source.bundleboard.image.dto;

import com.source.bundleboard.mediaresource.model.MimeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ImageShortInput(
        @NotBlank(message = "image file path is required")
        String filePath,

        @NotBlank(message = "image file name is required")
        String fileName,

        @NotNull(message = "image mime type is required")
        MimeType mimeType,

        @NotNull(message = "image width is required")
        Integer width,

        @NotNull(message = "image height is required")
        Integer height,

        @NotNull(message = "image file size is required")
        Long fileSize
) {
}
