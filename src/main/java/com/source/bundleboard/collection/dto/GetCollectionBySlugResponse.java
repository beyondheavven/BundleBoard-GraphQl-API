package com.source.bundleboard.collection.dto;

import java.math.BigDecimal;

public record GetCollectionBySlugResponse(

        Long id,

        String name,

        String description,

        BigDecimal price,

        String externalLink,

        String slug,

        Long authorId,

        Long mediaResourceId
) {
}
