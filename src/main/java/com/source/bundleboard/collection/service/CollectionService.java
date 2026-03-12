package com.source.bundleboard.collection.service;

import com.source.bundleboard.collection.dto.CollectionResponseDto;
import com.source.bundleboard.collection.dto.CreateNewCollectionDto;
import com.source.bundleboard.collection.dto.UpdateCollectionDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface CollectionService {


    Mono<CollectionResponseDto> getCollectionById(Long id);

    Flux<CollectionResponseDto> getAllCollections();

    Mono<CollectionResponseDto> createCollection(CreateNewCollectionDto collection);

    Mono<CollectionResponseDto> updateCollection(Long id, UpdateCollectionDto collection);

    Mono<Boolean> deleteCollection(Long id);


}
