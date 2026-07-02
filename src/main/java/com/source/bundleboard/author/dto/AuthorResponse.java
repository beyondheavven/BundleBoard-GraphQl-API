package com.source.bundleboard.author.dto;

import java.math.BigDecimal;
import java.util.List;

public record AuthorResponse(
        Long id,

        Long userId,

        String bio,

        BigDecimal rating,

        Integer totalSales,

        List<SocialLink> socialLinks,

        String username,

        String email,

        String avatarUrl
) {
}
