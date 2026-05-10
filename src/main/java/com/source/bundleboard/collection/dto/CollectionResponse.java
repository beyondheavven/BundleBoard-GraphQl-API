package com.source.bundleboard.collection.dto;

public record CollectionResponse(

        Long id,

        String name,

        String description,

        Double price,

        Long authorId,

        Long previewImageId
) {
}
