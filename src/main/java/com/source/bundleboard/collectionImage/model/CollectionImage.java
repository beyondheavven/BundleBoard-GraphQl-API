package com.source.bundleboard.collectionImage.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("collection_images")
public class CollectionImage {

    @Id
    @Column("id")
    private Long id;

    @Column("collection_id")
    private Long collectionId;

    @Column("image_id")
    private Long imageId;
}