package com.source.bundleboard.tag;

import com.source.bundleboard.tag.model.Tag;
import com.source.bundleboard.tag.repository.TagRepository;
import com.source.bundleboard.tag.service.TagServiceImpl;
import org.junit.jupiter.api.BeforeEach;
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
public class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagServiceImpl tagService;

    private Tag sampleTag1;
    private Tag sampleTag2;

    @BeforeEach
    void setUp() {
        sampleTag1 = new Tag();
        sampleTag1.setId(1L);
        sampleTag1.setName("Spring Boot");

        sampleTag2 = new Tag();
        sampleTag2.setId(2L);
        sampleTag2.setName("React");
    }

    // --- findById TESTS ---

    @Test
    void findById_Success() {
        when(tagRepository.findById(1L)).thenReturn(Mono.just(sampleTag1));

        StepVerifier.create(tagService.findById(1L))
                .expectNext(sampleTag1)
                .verifyComplete();

        verify(tagRepository).findById(1L);
    }

    @Test
    void findById_NullId_ReturnsEmptyMono() {
        StepVerifier.create(tagService.findById(null))
                .verifyComplete();

        verifyNoInteractions(tagRepository);
    }

    // --- findByName TESTS ---

    @Test
    void findByName_Success() {
        String tagName = "Spring Boot";
        when(tagRepository.findByName(tagName)).thenReturn(Mono.just(sampleTag1));

        StepVerifier.create(tagService.findByName(tagName))
                .expectNext(sampleTag1)
                .verifyComplete();

        verify(tagRepository).findByName(tagName);
    }

    @Test
    void findByName_NullOrBlank_ReturnsEmptyMono() {
        StepVerifier.create(tagService.findByName(null)).verifyComplete();
        StepVerifier.create(tagService.findByName("   ")).verifyComplete();
        StepVerifier.create(tagService.findByName("")).verifyComplete();

        verifyNoInteractions(tagRepository);
    }

    // --- findAll TESTS ---

    @Test
    void findAll_Success() {
        when(tagRepository.findAll()).thenReturn(Flux.just(sampleTag1, sampleTag2));

        StepVerifier.create(tagService.findAll())
                .expectNext(sampleTag1)
                .expectNext(sampleTag2)
                .verifyComplete();

        verify(tagRepository).findAll();
    }

    // --- findAllById TESTS ---

    @Test
    void findAllById_Success() {
        List<Long> ids = List.of(1L, 2L);
        when(tagRepository.findAllById(ids)).thenReturn(Flux.just(sampleTag1, sampleTag2));

        StepVerifier.create(tagService.findAllById(ids))
                .expectNext(sampleTag1)
                .expectNext(sampleTag2)
                .verifyComplete();

        verify(tagRepository).findAllById(ids);
    }

    @Test
    void findAllById_NullOrEmptyList_ReturnsEmptyFlux() {
        StepVerifier.create(tagService.findAllById(null)).verifyComplete();
        StepVerifier.create(tagService.findAllById(Collections.emptyList())).verifyComplete();

        verifyNoInteractions(tagRepository);
    }

    // --- findTagsByCollectionId TESTS ---

    @Test
    void findTagsByCollectionId_Success() {
        Long collectionId = 100L;
        when(tagRepository.findByCollectionId(collectionId)).thenReturn(Flux.just(sampleTag1, sampleTag2));

        StepVerifier.create(tagService.findTagsByCollectionId(collectionId))
                .expectNext(sampleTag1)
                .expectNext(sampleTag2)
                .verifyComplete();

        verify(tagRepository).findByCollectionId(collectionId);
    }

    @Test
    void findTagsByCollectionId_NullId_ReturnsEmptyFlux() {
        StepVerifier.create(tagService.findTagsByCollectionId(null))
                .verifyComplete();

        verifyNoInteractions(tagRepository);
    }

    // --- findAllTagsByCollectionId TESTS ---

    @Test
    void findAllTagsByCollectionId_Success() {
        Long collectionId = 200L;
        when(tagRepository.findAllByCollectionId(collectionId)).thenReturn(Flux.just(sampleTag1, sampleTag2));

        StepVerifier.create(tagService.findAllTagsByCollectionId(collectionId))
                .expectNext(sampleTag1)
                .expectNext(sampleTag2)
                .verifyComplete();

        verify(tagRepository).findAllByCollectionId(collectionId);
    }

    @Test
    void findAllTagsByCollectionId_NullId_ReturnsEmptyFlux() {
        StepVerifier.create(tagService.findAllTagsByCollectionId(null))
                .verifyComplete();

        verifyNoInteractions(tagRepository);
    }
}