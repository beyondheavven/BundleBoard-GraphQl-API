package com.source.bundleboard.collectionTag.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "collection_tags")
public class CollectionTag {

    @Id
    @Column("id")
    private Long id;

    @Column("tags_id")
    private Long tagsId;

    @Column("collections_id")
    private Long collectionsId;

}