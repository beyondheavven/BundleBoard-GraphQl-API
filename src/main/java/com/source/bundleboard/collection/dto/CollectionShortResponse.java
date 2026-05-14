package com.source.bundleboard.collection.dto;


import com.source.bundleboard.image.dto.ImageShortResponse;

public record CollectionShortResponse(

        Long id,

        String name,

        ImageShortResponse previewImage
) {
}
