package com.source.bundleboard.collection.controller;

import com.source.bundleboard.collection.dto.CollectionResponseDto;
import com.source.bundleboard.collection.dto.CreateNewCollectionDto;
import com.source.bundleboard.collection.dto.UpdateCollectionDto;
import com.source.bundleboard.collection.service.CollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    @QueryMapping
    public Flux<CollectionResponseDto> getAllCollections() {
        return collectionService.getAllCollections();
    }

    @QueryMapping
    public Mono<CollectionResponseDto> getCollectionById(@Argument Long id) {
        return collectionService.getCollectionById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR')")
    @MutationMapping
    public Mono<CollectionResponseDto> createCollection(@Argument(name = "input") CreateNewCollectionDto collection) {
        return collectionService.createCollection(collection);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR')")
    @MutationMapping
    public Mono<CollectionResponseDto> updateCollection(@Argument Long id, @Argument(name = "input") UpdateCollectionDto collection) {
        return collectionService.updateCollection(id, collection);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR')")
    @MutationMapping
    public Mono<Boolean> deleteCollection(@Argument Long id) {
        return collectionService.deleteCollection(id);
    }


}
