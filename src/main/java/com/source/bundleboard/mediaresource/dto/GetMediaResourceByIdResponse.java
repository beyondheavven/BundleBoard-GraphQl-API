package com.source.bundleboard.mediaresource.dto;

import com.source.bundleboard.mediaresource.model.MimeType;
import com.source.bundleboard.mediaresource.model.Provider;

public record GetMediaResourceByIdResponse(

        Long id,

        String fileName,

        Long fileSize,

        MimeType mimeType,

        Provider provider
) {
}
