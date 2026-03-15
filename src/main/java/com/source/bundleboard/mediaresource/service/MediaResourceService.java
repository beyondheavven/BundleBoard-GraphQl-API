package com.source.bundleboard.mediaresource.service;

import com.source.bundleboard.mediaresource.dto.MediaResourceResponseDto;
import reactor.core.publisher.Mono;

public interface MediaResourceService {

    Mono<MediaResourceResponseDto> findById(Long id);

}
