package com.source.bundleboard.mediaresource.service;

import com.source.bundleboard.mediaresource.dto.GetMediaResourceByIdResponse;
import com.source.bundleboard.mediaresource.model.MediaResource;
import com.stripe.net.HttpHeaders;
import reactor.core.publisher.Mono;

public interface MediaResourceService {

    Mono<GetMediaResourceByIdResponse> findGetMediaResourceById(Long id);

    Mono<MediaResource> findById(Long mediaResourceId);
}
