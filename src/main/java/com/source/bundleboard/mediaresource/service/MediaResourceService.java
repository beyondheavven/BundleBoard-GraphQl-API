package com.source.bundleboard.mediaresource.service;

import com.source.bundleboard.mediaresource.dto.GetMediaResourceByIdResponse;
import reactor.core.publisher.Mono;

public interface MediaResourceService {

    Mono<GetMediaResourceByIdResponse> findGetMediaResourceById(Long id);
}
