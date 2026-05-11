package com.source.bundleboard.collection.service;

import com.source.bundleboard.collection.dto.CollectionResponse;
import com.source.bundleboard.collection.dto.GetCollectionByIdResponse;
import com.source.bundleboard.collection.dto.CreateNewCollectionDto;
import com.source.bundleboard.collection.dto.UpdateCollectionDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface CollectionService {


    Mono<GetCollectionByIdResponse> getCollectionById(Long id);

    Flux<CollectionResponse> getAllCollections();

    Mono<GetCollectionByIdResponse> createCollection(CreateNewCollectionDto collection);

    Mono<GetCollectionByIdResponse> updateCollection(Long id, UpdateCollectionDto collection);

    Mono<Boolean> deleteCollection(Long id);


}
