package com.source.bundleboard.collection.dto;

import java.math.BigDecimal;

public record UpdateCollectionRequest(

        String name,

        String description,

        BigDecimal price

) {
}
