package com.source.bundleboard.author.dto;

import java.math.BigDecimal;

public record AuthorResponse(
        Long id,

        String bio,

        BigDecimal rating,

        Integer totalSales,

        String socialLinks
) {
}
