package com.source.bundleboard.collection.dto;

import com.source.bundleboard.image.dto.ImageShortResponse;

import java.math.BigDecimal;
import java.util.List;

public record AuthoredCollectionResponse(
        Long id,

        String name,

        BigDecimal price,

        String description
) {
}
