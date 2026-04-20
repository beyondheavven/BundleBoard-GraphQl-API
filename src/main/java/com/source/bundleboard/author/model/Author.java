package com.source.bundleboard.author.model;

import io.r2dbc.postgresql.codec.Json;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("authors")
public class Author {

    @Id
    @Column("users_id")
    Long userId;

    @Column("bio")
    String bio;

    @Column("social_links")
    Json socialLinks;

    @Column("rating")
    BigDecimal rating;

    @Column("total_sales")
    Integer totalSales;

    @Column("stripe_account_id")
    String stripeAccountId;
}
