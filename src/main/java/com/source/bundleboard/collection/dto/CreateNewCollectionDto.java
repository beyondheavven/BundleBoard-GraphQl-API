package com.source.bundleboard.collection.dto;

import java.math.BigDecimal;

public record CreateNewCollectionDto(

        String name,

        String description,

        Long authorId,

        Double price,

        String videoTutorialUrl,

        Long archiveId,

        Long previewImageId
) {
}
