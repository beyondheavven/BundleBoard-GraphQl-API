package com.source.bundleboard.collection.dto;

import com.source.bundleboard.image.dto.ImageShortResponse;

import java.math.BigDecimal;
import java.util.List;

public record CollectionResponse(

        Long id,

        String name,

        String description,

        BigDecimal price,

        Long authorId,

        List<ImageShortResponse> galleryImages,

        Long likesCount,

        Boolean isLiked
) {
}
