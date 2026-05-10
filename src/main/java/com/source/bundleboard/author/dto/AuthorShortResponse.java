package com.source.bundleboard.author.dto;

public record AuthorShortResponse(

        Long id,

        java.math.BigDecimal rating,

        Integer totalSales,

        String username
) {
}
