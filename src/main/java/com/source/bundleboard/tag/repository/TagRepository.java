package com.source.bundleboard.tag.repository;

import com.source.bundleboard.tag.model.Tag;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface TagRepository extends R2dbcRepository<Tag, Long> {

    Mono<Tag> findByName(String name);

    @Query("SELECT t.* FROM tags t " +
            "INNER JOIN collection_tags ct ON t.id = ct.tags_id " +
            "WHERE ct.collections_id = :collectionId")
    Flux<Tag> findByCollectionId(Long collectionId);
}
