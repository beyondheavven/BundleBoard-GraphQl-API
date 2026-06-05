package com.source.bundleboard.collection.dto;

import com.source.bundleboard.image.model.PreviewImage;

import java.math.BigDecimal;
import java.util.List;

public record UpdateCollectionRequest(

        String name,

        String description,

        BigDecimal price,

        List<PreviewImage> galleryImages

) {
}
