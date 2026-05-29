package com.source.bundleboard.collection.dto;

import java.math.BigDecimal;

public record CollectionWithImageRow(
        Long id,
        String name,
        BigDecimal price,
        String description,
        String previewFilePath,
        String previewFileName
) {
}
