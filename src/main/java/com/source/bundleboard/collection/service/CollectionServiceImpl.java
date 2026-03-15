package com.source.bundleboard.collection.service;

import com.source.bundleboard.api.exception.CollectionNotFoundException;
import com.source.bundleboard.api.exception.DescriptionException;
import com.source.bundleboard.api.exception.MinimalPriceException;
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

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CollectionServiceImpl implements CollectionService {

    private final CollectionRepository collectionRepository;

    private final AuthorService authorService;

    private final CollectionMapper collectionMapper;

    private static final BigDecimal MIN_PRICE = new BigDecimal("5.00");

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
                .switchIfEmpty(Mono.error(new MinimalPriceException("Validation failed: Minimal price is 5 USD")))
                .filter(c -> c.description().length() >= 200 && c.description().length() <= 1000)
                .switchIfEmpty(Mono.error(new DescriptionException("Validation failed: Description 200-1000 chars")))
                .flatMap(dto -> authorService.findById(dto.authorId()).thenReturn(dto))
                .map(collectionMapper::toEntity)
                .flatMap(collectionRepository::save)
                .map(collectionMapper::toDto);
    }

    @Override
    public Mono<CollectionResponseDto> updateCollection(Long id, UpdateCollectionDto collection) {
        return collectionRepository.findCollectionById(id)
                .switchIfEmpty(Mono.error(new CollectionNotFoundException("Collection not found")))
                .flatMap(entity -> {
                    if(entity.price().compareTo(MIN_PRICE) < 0){
                        return Mono.error(new MinimalPriceException("Minimal price is 5 USD"));
                    }

                    if(entity.description().length() < 200 || entity.description().length() > 1000){
                        return Mono.error(new DescriptionException("Description must be between 200 and 1000 characters"));
                    }

                    collectionMapper.updateEntityFromDto(collection, entity);
                    return collectionRepository.save(entity);
                })
                .map(collectionMapper::toDto);
    }

    @Override
    public Mono<Boolean> deleteCollection(Long id) {
        return collectionRepository.deleteById(id).thenReturn(true).defaultIfEmpty(false);
    }


}
