package com.source.bundleboard.collection.dto;

import java.util.List;

public record CollectionByTagResponse(

        List<CollectionResponse> collections,

        int totalPages,

        long totalElements
) {
}
