package com.source.bundleboard.collectionImage.service;

import com.source.bundleboard.collectionImage.model.CollectionImage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CollectionImageService {

    Flux<CollectionImage> saveAll(List<CollectionImage> collectionImages);

    Mono<Void> deleteAllByCollectionId(Long collectionId);
}
