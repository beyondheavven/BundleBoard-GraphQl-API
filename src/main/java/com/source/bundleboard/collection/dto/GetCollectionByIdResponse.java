package com.source.bundleboard.collection.dto;

import java.math.BigDecimal;

public record GetCollectionByIdResponse(

        Long id,

        String name,

        String description,

        BigDecimal price,

        String videoTutorialUrl,

        String externalLink,

        Long authorId,

        Long mediaResourceId
) {
}
