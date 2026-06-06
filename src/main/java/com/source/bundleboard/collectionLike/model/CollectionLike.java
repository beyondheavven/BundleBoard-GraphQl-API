package com.source.bundleboard.collectionLike.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="collection_likes")
public class CollectionLike {

    @Id
    private Long id;

    @Column("collection_id")
    private Long collectionId;

    @Column("author_id")
    private Long authorId;

    @Column("created_at")
    private Instant createdAt;
}
