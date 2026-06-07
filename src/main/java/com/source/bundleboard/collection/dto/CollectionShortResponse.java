package com.source.bundleboard.collection.dto;


import com.source.bundleboard.image.dto.ImageShortResponse;

import java.util.List;

public record CollectionShortResponse(

        Long id,

        String name,

        List<ImageShortResponse> galleryImages
) {
}
