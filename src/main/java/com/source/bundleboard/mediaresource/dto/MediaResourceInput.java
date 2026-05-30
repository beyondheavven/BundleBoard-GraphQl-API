package com.source.bundleboard.mediaresource.dto;

import com.source.bundleboard.mediaresource.model.MimeType;
import com.source.bundleboard.mediaresource.model.Provider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MediaResourceInput(
        @NotBlank(message = "archive filename is required")
        String fileName,

        @NotBlank(message = "archive filepath is required")
        String filePath,

        @NotNull(message = "archive mime type is required")
        MimeType mimeType,

        @NotNull(message = "storage provider is required")
        Provider provider,

        @NotNull(message = "filesize is required")
        @Positive(message = "filesize must be greater than zero")
        Long fileSize
) {
}
