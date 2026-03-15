package com.source.bundleboard.mediaresource.service;

import com.source.bundleboard.mediaresource.dto.MediaResourceResponseDto;
import com.source.bundleboard.mediaresource.mapper.MediaResourceMapper;
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
    public Mono<MediaResourceResponseDto> findById(Long id) {
        return mediaResourceRepository.findById(id)
                .map(mediaResourceMapper::toDto)
                .switchIfEmpty(Mono.error(new RuntimeException("Media resource not found")));
    }
}
