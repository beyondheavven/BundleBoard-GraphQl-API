package com.source.bundleboard.collection.service;

import com.source.bundleboard.collection.dto.CollectionResponse;
import com.source.bundleboard.collection.dto.GetCollectionResponse;
import com.source.bundleboard.collection.dto.CreateNewCollectionDto;
import com.source.bundleboard.collection.dto.UpdateCollectionDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface CollectionService {


    Mono<GetCollectionResponse> getCollectionById(Long id);

    Flux<CollectionResponse> getAllCollections();

    Mono<GetCollectionResponse> createCollection(CreateNewCollectionDto collection);

    Mono<GetCollectionResponse> updateCollection(Long id, UpdateCollectionDto collection);

    Mono<Boolean> deleteCollection(Long id);


}
