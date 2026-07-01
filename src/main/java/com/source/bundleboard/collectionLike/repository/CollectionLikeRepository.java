package com.source.bundleboard.collectionLike.repository;

import com.source.bundleboard.collectionLike.model.CollectionLike;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


@Repository
public interface CollectionLikeRepository extends R2dbcRepository<CollectionLike, Long> {

    Mono<Integer> countByCollectionId(Long collectionId);

    Mono<CollectionLike> findByCollectionIdAndUserId(Long collectionId, Long id);

    Mono<Boolean> existsByCollectionIdAndUserId(Long collectionId, Long id);
}
