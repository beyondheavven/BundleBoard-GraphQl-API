package com.source.bundleboard.image.service;

import com.source.bundleboard.image.dto.PreviewImageResponseDto;
import reactor.core.publisher.Mono;

public interface PreviewImageService {

    Mono<PreviewImageResponseDto> findByImageId(Long id);
}
