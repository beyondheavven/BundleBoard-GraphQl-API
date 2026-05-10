package com.source.bundleboard.image.dto;

public record ImageResponse(
        Long id,

        String fileName,

        String filePath,

        String mimeType,

        Integer width,

        Integer height
) {
}
