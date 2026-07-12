package com.source.bundleboard.collection.dto;

public record CollectionCommentResponse(

        Long id,

        String name,

        String slug,

        Long authorId
) {
}
