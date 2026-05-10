package com.source.bundleboard.image.dto;

import java.util.List;

public record BulkImageResponse(

        List<UploadImageResponse> images,

        Integer totalCount,

        Long totalSize,

        UploadStatus uploadStatus


) {
}
