package com.source.bundleboard.collection.dto;

import java.math.BigDecimal;

public record GetCollectionResponse(

        Long id,

        String name,

        String description,

        BigDecimal price,

        String videoTutorialUrl,

        Long authorId,

        Long previewImageId,

        Long mediaResourceId
) {
}
