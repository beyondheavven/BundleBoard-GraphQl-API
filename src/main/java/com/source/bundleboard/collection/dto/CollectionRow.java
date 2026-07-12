package com.source.bundleboard.collection.dto;

import java.math.BigDecimal;

public record CollectionRow(
        Long id,

        String name,

        BigDecimal price,

        String description,

        String slug,

        Long authorId
) {
}
