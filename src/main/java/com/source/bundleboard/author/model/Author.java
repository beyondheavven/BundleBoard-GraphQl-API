package com.source.bundleboard.author.model;

import io.r2dbc.postgresql.codec.Json;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table("authors")
public class Author {

    @Id
    @Column("id")
    Long id;

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
