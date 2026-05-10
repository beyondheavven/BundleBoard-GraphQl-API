package com.source.bundleboard.image.dto;

import com.source.bundleboard.mediaresource.model.MimeType;

public record BaseImageResponse(

        Long id,

        String fileName,

        String filePath,

        MimeType mimeType,

        Integer width,

        Integer height,

        Long fileSize

) {
}
