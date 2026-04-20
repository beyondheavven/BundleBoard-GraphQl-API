package com.source.bundleboard.collection.model;

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
@Table("collections")
public class Collection {

    @Id
    @Column("id")
    Long id;

    @Column("name")
    String name;

    @Column("description")
    String description;

    @Column("price")
    BigDecimal price;

    @Column("video_tutorial_url")
    String videoTutorialUrl;

    @Column("authors_id")
    Long authorId;

    @Column("project_file_id")
    Long mediaResourceId;

    @Column("preview_image_id")
    Long previewImageId;

}
