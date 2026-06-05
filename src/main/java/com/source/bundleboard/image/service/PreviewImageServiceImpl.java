package com.source.bundleboard.image.service;

import com.source.bundleboard.api.exception.ImageNotFoundException;
import com.source.bundleboard.image.dto.BaseImageResponse;
import com.source.bundleboard.image.dto.ImageShortResponse;
import com.source.bundleboard.image.mapper.PreviewImageMapper;
import com.source.bundleboard.image.model.PreviewImage;
import com.source.bundleboard.image.repository.PreviewImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PreviewImageServiceImpl implements PreviewImageService {

    private final PreviewImageRepository previewImageRepository;

    private final PreviewImageMapper previewImageMapper;

    @Override
    public Mono<BaseImageResponse> findByImageId(Long id) {
        return previewImageRepository.findById(id)
                        .map(previewImageMapper::toDto)
                                .switchIfEmpty(Mono.error(new ImageNotFoundException()));
    }

    @Override
    public Mono<PreviewImage> save(PreviewImage image) {
        return previewImageRepository.save(image).switchIfEmpty(Mono.error(new ImageNotFoundException()));
    }

    @Override
    public Mono<ImageShortResponse> findShortResponseById(Long id) {
        return previewImageRepository.findById(id)
                .map(previewImageMapper::toShortDto)
                .switchIfEmpty(Mono.error(new ImageNotFoundException()));
    }

    @Override
    public Flux<PreviewImage> saveAll(List<PreviewImage> newImages) {
        if (newImages == null || newImages.isEmpty()) {
            return Flux.empty();
        }
        return previewImageRepository.saveAll(newImages);
    }

    @Override
    public Flux<PreviewImage> findAllByCollectionId(Long collectionId) {
        if (collectionId == null) {
            return Flux.empty();
        }
        return previewImageRepository.findAllByCollectionId(collectionId);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        if (id == null) {
            return Mono.empty();
        }
        return previewImageRepository.deleteById(id);
    }

    @Override
    public Flux<ImageShortResponse> findAllShortResponsesByCollectionId(Long collectionId) {
        if (collectionId == null) {
            return Flux.empty();
        }
        return previewImageRepository.findAllByCollectionId(collectionId)
                .map(image -> new ImageShortResponse(
                        image.getFilePath(),
                        image.getFileName()
                ));
    }

}
