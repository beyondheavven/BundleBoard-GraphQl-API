package com.source.bundleboard.image.service;

import com.source.bundleboard.image.dto.BaseImageResponse;
import com.source.bundleboard.image.dto.ImageShortResponse;
import com.source.bundleboard.image.model.PreviewImage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PreviewImageService {

    Mono<BaseImageResponse> findByImageId(Long id);

    Mono<PreviewImage> save(PreviewImage image);

    Mono<ImageShortResponse> findShortResponseById(Long id);

    Mono<List<PreviewImage>> saveAll(List<PreviewImage> newImages);

    Flux<PreviewImage> findAllByCollectionId(Long collectionId);

    Mono<Void> deleteById(Long id);
}
