package com.source.bundleboard.collection.service;

import com.source.bundleboard.collection.dto.*;
import org.springframework.security.config.annotation.web.PortMapperDsl;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface CollectionService {


    Mono<GetCollectionByIdResponse> getCollectionById(Long id);

    Flux<CollectionResponse> getAllCollections();

    Mono<CreateCollectionResponse> createCollection(CreateNewCollectionInput input, String username);

    Mono<GetCollectionByIdResponse> updateCollection(Long id, UpdateCollectionDto collection);

    Mono<Boolean> deleteCollection(Long id);

    Mono<CollectionShortResponse> findShortResponseById(Long collectionId);

    Flux<AuthoredCollectionResponse> findAllByAuthorId(Long authorId);
}
