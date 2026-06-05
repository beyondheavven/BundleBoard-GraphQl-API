package com.source.bundleboard.collectionImage.service;

import com.source.bundleboard.collectionImage.model.CollectionImage;
import com.source.bundleboard.collectionImage.repository.CollectionImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CollectionImageServiceImpl implements CollectionImageService {

    private final CollectionImageRepository collectionImageRepository;


    @Override
    public Flux<CollectionImage> saveAll(List<CollectionImage> collectionImages) {
        if (collectionImages == null || collectionImages.isEmpty()) {
            return Flux.empty();
        }
        return collectionImageRepository.saveAll(collectionImages);
    }

    @Override
    public Mono<Void> deleteAllByCollectionId(Long collectionId) {
        return collectionImageRepository.deleteAllByCollectionId(collectionId);
    }
}
