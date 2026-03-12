package com.source.bundleboard.collection.dto;

public record UpdateCollectionDto(

        String name,

        String description,

        Double price,

        String videoTutorialUrl,

        Long previewImageId
) {
}
