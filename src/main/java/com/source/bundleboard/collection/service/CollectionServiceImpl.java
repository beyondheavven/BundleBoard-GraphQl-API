package com.source.bundleboard.collection.service;

import com.source.bundleboard.api.exception.CollectionNotFoundException;
import com.source.bundleboard.author.service.AuthorService;
import com.source.bundleboard.collection.dto.CollectionResponseDto;
import com.source.bundleboard.collection.dto.CreateNewCollectionDto;
import com.source.bundleboard.collection.dto.UpdateCollectionDto;
import com.source.bundleboard.collection.mapper.CollectionMapper;
import com.source.bundleboard.collection.repository.CollectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CollectionServiceImpl implements CollectionService {

    private final CollectionRepository collectionRepository;

    private final AuthorService authorService;

    private final CollectionMapper collectionMapper;

    @Override
    public Mono<CollectionResponseDto> getCollectionById(Long id) {
        return collectionRepository.findCollectionById(id)
                .map(collectionMapper::toDto)
                .switchIfEmpty(Mono.error(new CollectionNotFoundException("Collection not found.")));
    }

    @Override
    public Flux<CollectionResponseDto> getAllCollections() {
        return collectionRepository.findAll()
                .map(collectionMapper::toDto)
                .switchIfEmpty(Flux.error(new CollectionNotFoundException("No collections found.")));
    }

    @Transactional
    @Override
    public Mono<CollectionResponseDto> createCollection(CreateNewCollectionDto collection) {
        return Mono.just(collection)
                .filter(d -> d.price() >= 5.0)
                .filter(c -> c.description().length() >= 200 && c.description().length() <= 1000)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Validation failed: Price min 5 USD, Description 200-1000 chars")))
                .flatMap(dto -> authorService.existsById(dto.authorId()).thenReturn(dto))
                .map(collectionMapper::toEntity)
                .flatMap(collectionRepository::save)
                .map(collectionMapper::toDto);
    }

    @Override
    public Mono<CollectionResponseDto> updateCollection(Long id, UpdateCollectionDto collection) {
        return null;
    }

    @Override
    public Mono<Void> deleteCollection(Long id) {
        return null;
    }


}
