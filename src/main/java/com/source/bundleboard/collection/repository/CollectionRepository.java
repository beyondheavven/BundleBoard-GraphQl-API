package com.source.bundleboard.collection.repository;

import com.source.bundleboard.collection.dto.CollectionWithImageRow;
import com.source.bundleboard.collection.model.Collection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository
public interface CollectionRepository extends R2dbcRepository<Collection, Long> {

    Mono<Collection> findCollectionById(Long id);

    @Query("""
        SELECT 
            c.id AS id, 
            c.name AS name, 
            c.price AS price, 
            c.description AS description, 
            i.file_path AS preview_file_path, 
            i.file_name AS preview_file_name
        FROM collections c
        LEFT JOIN images i ON c.preview_image_id = i.id
        WHERE c.authors_id = :authorId
    """)
    Flux<CollectionWithImageRow> findAllByAuthorId(Long authorId);

    Flux<Collection> findAllBy(Pageable pageable);

    @Query("""
        SELECT c.* FROM collections c
        INNER JOIN collection_tags ct ON c.id = ct.collections_id
        INNER JOIN tags t ON ct.tags_id = t.id
        WHERE t.name = :tagName
        LIMIT :limit OFFSET :offset
    """)
    Flux<Collection> findCollectionsByTagNamePaged(String tagName, int limit, int offset);

    @Query("""
        SELECT COUNT(c.id) FROM collections c
        INNER JOIN collection_tags ct ON c.id = ct.collections_id
        INNER JOIN tags t ON ct.tags_id = t.id
        WHERE t.name = :tagName
    """)
    Mono<Long> countCollectionsByTagName(String tagName);
}
