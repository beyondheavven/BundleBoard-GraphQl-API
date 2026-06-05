package com.source.bundleboard.collection.service;

import com.source.bundleboard.collection.dto.*;
import com.source.bundleboard.collection.model.Collection;
import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


public interface CollectionService {


    Mono<GetCollectionByIdResponse> getCollectionById(Long id);

    Flux<CollectionResponse> getAllCollections(int page, int size);

    Mono<CreateCollectionResponse> createCollection(CreateNewCollectionInput input, String username);

    Mono<Boolean> deleteCollection(Long id, String folderPath);

    Mono<CollectionShortResponse> findShortResponseById(Long collectionId);

    Flux<AuthoredCollectionResponse> findAllByAuthorId(Long authorId);

    Mono<CollectionByTagResponse> getCollectionByTagName(@Valid CollectionFilterInput input);

     Flux<Collection> findAllByIds(List<Long> ids);

    Mono<Collection> findById(Long collectionId);

    Mono<GetCollectionByIdResponse> updateCollection(Long id, UpdateCollectionRequest collection);
}
