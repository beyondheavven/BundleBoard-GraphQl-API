package com.source.bundleboard.author.model;

import io.r2dbc.postgresql.codec.Json;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("authors")
public record Author(

        @Id
        @Column("users_id")
        Long userId,

        @Column("bio")
        String bio,

        @Column("social_links")
        Json socialLinks,

        @Column("rating")
        BigDecimal rating,

        @Column("total_sales")
        Integer totalSales,

        @Column("stripe_account_id")
        String stripeAccountId
) {
}
