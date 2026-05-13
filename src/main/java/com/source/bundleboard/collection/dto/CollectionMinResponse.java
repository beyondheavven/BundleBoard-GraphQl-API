package com.source.bundleboard.collection.dto;


import com.source.bundleboard.image.dto.ImageShortResponse;

public record CollectionMinResponse(

        Long id,

        String name,

        ImageShortResponse previewImage
) {
}
