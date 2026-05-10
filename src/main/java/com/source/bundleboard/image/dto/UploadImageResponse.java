package com.source.bundleboard.image.dto;

import com.source.bundleboard.mediaresource.model.MimeType;

public record UploadImageResponse(

        Long id,

        String fileName,

        String filePath,

        MimeType mimeType,

        Long fileSize
) {
}
