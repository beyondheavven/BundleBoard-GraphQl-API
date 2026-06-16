package com.source.bundleboard.collectionImage;

import com.source.bundleboard.collectionImage.model.CollectionImage;
import com.source.bundleboard.collectionImage.repository.CollectionImageRepository;
import com.source.bundleboard.collectionImage.service.CollectionImageServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;


@ExtendWith(MockitoExtension.class)
public class CollectionImageServiceTest {

    @Mock
    private CollectionImageRepository collectionImageRepository;

    @InjectMocks
    private CollectionImageServiceImpl collectionImageService;

    @Test
    void saveAll_Success() {
        List<CollectionImage> images = List.of(
                new CollectionImage(1L, 100L, 200L),
                new CollectionImage(2L, 100L, 201L)
        );

        when(collectionImageRepository.saveAll(images)).thenReturn(Flux.fromIterable(images));

        StepVerifier.create(collectionImageService.saveAll(images))
                .expectNextCount(2)
                .verifyComplete();

        verify(collectionImageRepository).saveAll(images);
    }

    @Test
    void saveAll_EmptyList_ReturnsEmptyFlux() {
        StepVerifier.create(collectionImageService.saveAll(Collections.emptyList()))
                .verifyComplete();

        verifyNoInteractions(collectionImageRepository);
    }

    @Test
    void saveAll_NullList_ReturnsEmptyFlux() {
        StepVerifier.create(collectionImageService.saveAll(null))
                .verifyComplete();

        verifyNoInteractions(collectionImageRepository);
    }

    @Test
    void deleteAllByCollectionId_Success() {
        Long collectionId = 100L;
        when(collectionImageRepository.deleteAllByCollectionId(collectionId)).thenReturn(Mono.empty());

        StepVerifier.create(collectionImageService.deleteAllByCollectionId(collectionId))
                .verifyComplete();

        verify(collectionImageRepository).deleteAllByCollectionId(collectionId);
    }
}