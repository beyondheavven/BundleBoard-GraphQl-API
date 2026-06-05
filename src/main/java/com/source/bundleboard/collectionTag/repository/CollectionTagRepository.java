package com.source.bundleboard.collectionTag.repository;

import com.source.bundleboard.collectionTag.model.CollectionTag;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CollectionTagRepository extends R2dbcRepository<CollectionTag, Long> {

    Mono<Void> deleteAllByCollectionsId(Long collectionsId);
}
