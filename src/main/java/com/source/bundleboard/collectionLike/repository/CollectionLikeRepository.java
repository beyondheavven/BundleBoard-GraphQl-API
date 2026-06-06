package com.source.bundleboard.collectionLike.repository;

import com.source.bundleboard.collectionLike.model.CollectionLike;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


@Repository
public interface CollectionLikeRepository extends R2dbcRepository<CollectionLike, Long> {

    Mono<CollectionLike> findByCollectionIdAndAuthorId(Long collectionId, Long authorId);

    Mono<Integer> countByCollectionId(Long collectionId);

    Mono<Boolean> existsByCollectionIdAndAuthorId(Long collectionId, Long authorId);
}
