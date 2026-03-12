package com.source.bundleboard.collection.dto;

import java.math.BigDecimal;

public record CollectionResponseDto(

        Long id,

        String name,

        String description,

        Long authorId,

        BigDecimal price,

        Long previewImageId
) {
}
