package com.source.bundleboard.image.dto;

import com.source.bundleboard.mediaresource.MimeType;

public record PreviewImageResponseDto(

        Long id,

        String fileName,

        String filePath,

        MimeType mimeType,

        Integer width,

        Integer height,

        Long fileSize

) {
}
