package com.source.bundleboard.mediaresource.service;

import com.source.bundleboard.api.exception.MediaResourceNotFoundException;
import com.source.bundleboard.mediaresource.dto.GetMediaResourceByIdResponse;
import com.source.bundleboard.mediaresource.mapper.MediaResourceMapper;
import com.source.bundleboard.mediaresource.model.MediaResource;
import com.source.bundleboard.mediaresource.repository.MediaResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MediaResourceServiceImpl implements MediaResourceService {

    private final MediaResourceRepository mediaResourceRepository;

    private final MediaResourceMapper mediaResourceMapper;


    @Override
    public Mono<GetMediaResourceByIdResponse> findGetMediaResourceById(Long id) {
        return mediaResourceRepository.findById(id)
                .map(mediaResourceMapper::toDto)
                .switchIfEmpty(Mono.error(new MediaResourceNotFoundException(id)));
    }

    @Override
    public Mono<MediaResource> findById(Long mediaResourceId) {
        if (mediaResourceId == null) {
            return Mono.empty();
        }

        return mediaResourceRepository.findById(mediaResourceId);
    }
}
