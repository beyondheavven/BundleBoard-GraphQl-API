package com.source.bundleboard.image.service;

import com.source.bundleboard.api.exception.ImageNotFoundException;
import com.source.bundleboard.image.dto.PreviewImageResponseDto;
import com.source.bundleboard.image.mapper.PreviewImageMapper;
import com.source.bundleboard.image.model.PreviewImage;
import com.source.bundleboard.image.repository.PreviewImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PreviewImageServiceImpl implements PreviewImageService {

    private final PreviewImageRepository previewImageRepository;

    private final PreviewImageMapper previewImageMapper;

    @Override
    public Mono<PreviewImageResponseDto> findByImageId(Long id) {
        return previewImageRepository.findById(id)
                        .map(previewImageMapper::toDto)
                                .switchIfEmpty(Mono.error(new ImageNotFoundException()));
    }

    @Override
    public Mono<PreviewImage> save(PreviewImage image) {
        return previewImageRepository.save(image).switchIfEmpty(Mono.error(new ImageNotFoundException()));
    }

}
