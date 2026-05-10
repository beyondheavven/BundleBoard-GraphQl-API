package com.source.bundleboard.image.service;

import com.source.bundleboard.image.dto.BaseImageResponse;
import com.source.bundleboard.image.dto.ImageShortResponse;
import com.source.bundleboard.image.model.PreviewImage;
import reactor.core.publisher.Mono;

public interface PreviewImageService {

    Mono<BaseImageResponse> findByImageId(Long id);

    Mono<PreviewImage> save(PreviewImage image);

    Mono<ImageShortResponse> findShortResponseById(Long id);
}
