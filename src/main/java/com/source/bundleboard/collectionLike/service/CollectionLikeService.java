package com.source.bundleboard.collectionLike.service;

import reactor.core.publisher.Mono;

public interface CollectionLikeService {

    Mono<Boolean> toggleFavorite(Long collectionId, String username);

    Mono<Integer> countByCollectionId(Long id);

    Mono<Boolean> isLikedByCurrentUser(Long id);
}
