package com.source.bundleboard.collectionTag;

import com.source.bundleboard.collectionTag.model.CollectionTag;
import com.source.bundleboard.collectionTag.repository.CollectionTagRepository;
import com.source.bundleboard.collectionTag.serivce.CollectionTagServiceImpl;
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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CollectionTagServiceTest {

    @Mock
    private CollectionTagRepository collectionTagRepository;

    @InjectMocks
    private CollectionTagServiceImpl collectionTagService;

    // --- saveAll TESTS ---

    @Test
    void saveAll_Success() {
        CollectionTag tag1 = new CollectionTag();
        CollectionTag tag2 = new CollectionTag();
        List<CollectionTag> tags = List.of(tag1, tag2);

        when(collectionTagRepository.saveAll(tags)).thenReturn(Flux.fromIterable(tags));

        StepVerifier.create(collectionTagService.saveAll(tags))
                .expectNextCount(2)
                .verifyComplete();

        verify(collectionTagRepository).saveAll(tags);
    }

    @Test
    void saveAll_EmptyList_ReturnsEmptyFlux() {
        StepVerifier.create(collectionTagService.saveAll(Collections.emptyList()))
                .verifyComplete();
        verifyNoInteractions(collectionTagRepository);
    }

    @Test
    void saveAll_NullList_ReturnsEmptyFlux() {
        StepVerifier.create(collectionTagService.saveAll(null))
                .verifyComplete();
        verifyNoInteractions(collectionTagRepository);
    }

    // --- deleteAllByCollectionsId TESTS ---

    @Test
    void deleteAllByCollectionsId_Success() {
        Long collectionId = 123L;
        when(collectionTagRepository.deleteAllByCollectionsId(collectionId)).thenReturn(Mono.empty());
        StepVerifier.create(collectionTagService.deleteAllByCollectionsId(collectionId))
                .verifyComplete();
        verify(collectionTagRepository).deleteAllByCollectionsId(collectionId);
    }
}