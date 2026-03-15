package com.source.bundleboard.tag.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "tags")
public record Tag(

        @Id
        @Column("id")
        Long id,

        @Column("name")
        String name
) {
}
