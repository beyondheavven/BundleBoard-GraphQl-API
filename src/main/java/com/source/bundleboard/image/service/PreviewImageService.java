package com.source.bundleboard.image.service;

import com.source.bundleboard.image.dto.PreviewImageResponseDto;
import com.source.bundleboard.image.dto.UploadImageResponse;
import com.source.bundleboard.image.model.PreviewImage;
import reactor.core.publisher.Mono;

public interface PreviewImageService {

    Mono<PreviewImageResponseDto> findByImageId(Long id);

    Mono<PreviewImage> save(PreviewImage image);
}
