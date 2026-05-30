package com.source.bundleboard.collection.dto;

public record CreateCollectionResponse(
        Long id,
        String name,
        boolean success
) {
}
