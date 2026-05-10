package com.source.bundleboard.author.dto;

public record AuthorShortResponse(
        Long id,

        Double rating,

        Integer totalSales,

        String username
) {
}
