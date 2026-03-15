package com.source.bundleboard.mediaresource.dto;

import com.source.bundleboard.mediaresource.model.MimeType;
import com.source.bundleboard.mediaresource.model.Provider;

public record MediaResourceResponseDto(

        Long id,

        String fileName,

        String filePath,

        MimeType mimeType,

        Provider provider,

        Long fileSize
) {
}
