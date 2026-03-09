package com.source.bundleboard.collection.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("collections")
public record Collection(

        @Id
        @Column("id")
        Long Id,

        @Column("name")
        String name,

        @Column("description")
        String description,

        @Column("price")
        BigDecimal price,

        @Column("video_tutorial_url")
        String videoTutorialUrl,

        @Column("author_id")
        Long authorId,

        @Column("project_file_id")
        Long mediaResourceId,

        @Column("preview_image_id")
        Long previewImageId

) {
}
