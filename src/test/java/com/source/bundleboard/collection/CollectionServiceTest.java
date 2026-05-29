package com.source.bundleboard.collection;

import com.source.bundleboard.api.exception.CollectionNotFoundException;
import com.source.bundleboard.api.exception.DescriptionException;
import com.source.bundleboard.api.exception.MinimalPriceException;
import com.source.bundleboard.author.model.Author;
import com.source.bundleboard.author.repository.AuthorRepository;
import com.source.bundleboard.collection.dto.*;
import com.source.bundleboard.collection.mapper.CollectionMapper;
import com.source.bundleboard.collection.model.Collection;
import com.source.bundleboard.collection.repository.CollectionRepository;
import com.source.bundleboard.collection.service.CollectionServiceImpl;
import com.source.bundleboard.image.dto.ImageShortResponse;
import com.source.bundleboard.image.service.PreviewImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CollectionServiceTest {

    @Mock
    private CollectionRepository collectionRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private CollectionMapper collectionMapper;

    @Mock
    private PreviewImageService previewImageService;

    @InjectMocks
    private CollectionServiceImpl collectionService;

    private Collection sampleCollection;
    private GetCollectionByIdResponse sampleGetDto;

    @BeforeEach
    void setUp() {
        sampleCollection = new Collection();
        sampleCollection.setId(1L);
        sampleCollection.setName("Java Bundle");
        sampleCollection.setDescription("A comprehensive collection of Java reactive tutorials and bundles for developers.");
        sampleCollection.setPrice(new BigDecimal("10.00"));
        sampleCollection.setPreviewImageId(100L);

        sampleGetDto = new GetCollectionByIdResponse(
                1L,
                "Java Bundle",
                "A comprehensive collection of Java reactive tutorials and bundles for developers.",
                new BigDecimal("10.00"),
                "http://tutorials.com/1",
                42L,
                100L,
                200L
        );
    }


    @Test
    void getCollectionById_Success() {
        when(collectionRepository.findCollectionById(1L)).thenReturn(Mono.just(sampleCollection));
        when(collectionMapper.toGetDto(sampleCollection)).thenReturn(sampleGetDto);

        StepVerifier.create(collectionService.getCollectionById(1L))
                .expectNext(sampleGetDto)
                .verifyComplete();
    }

    @Test
    void getCollectionById_NotFound_ThrowsException() {
        when(collectionRepository.findCollectionById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(collectionService.getCollectionById(1L))
                .expectError(CollectionNotFoundException.class)
                .verify();
    }

    @Test
    void getAllCollections_Success() {
        CollectionResponse responseDto = new CollectionResponse(1L, "Java Bundle", "Desc", 10.0, null, null);

        when(collectionRepository.findAll()).thenReturn(Flux.just(sampleCollection));
        when(collectionMapper.toDto(sampleCollection)).thenReturn(responseDto);

        StepVerifier.create(collectionService.getAllCollections())
                .expectNext(responseDto)
                .verifyComplete();
    }


    @Test
    void createCollection_Success() {
        CreateNewCollectionDto inputDto = new CreateNewCollectionDto(
                "New Pack",
                "Valid description that is longer than 5 chars",
                42L,
                15.0,
                "http://url",
                200L,
                100L
        );

        when(authorRepository.findById(42L)).thenReturn(Mono.just(new Author()));
        when(collectionMapper.toEntity(inputDto)).thenReturn(sampleCollection);
        when(collectionRepository.save(sampleCollection)).thenReturn(Mono.just(sampleCollection));
        when(collectionMapper.toGetDto(sampleCollection)).thenReturn(sampleGetDto);

        StepVerifier.create(collectionService.createCollection(inputDto))
                .expectNext(sampleGetDto)
                .verifyComplete();
    }

    @Test
    void createCollection_LowPrice_ThrowsMinimalPriceException() {
        CreateNewCollectionDto inputDto = new CreateNewCollectionDto(
                "Cheap Pack",
                "Valid description",
                42L,
                4.99,
                null,
                200L,
                100L
        );

        StepVerifier.create(collectionService.createCollection(inputDto))
                .expectErrorMatches(throwable -> throwable instanceof MinimalPriceException
                        && throwable.getMessage().contains("Minimal price is 5 USD"))
                .verify();

        verifyNoInteractions(authorRepository, collectionRepository, collectionMapper);
    }

    @Test
    void createCollection_ShortDescription_ThrowsDescriptionException() {
        CreateNewCollectionDto inputDto = new CreateNewCollectionDto(
                "Pack",
                "123",
                42L,
                10.0,
                null,
                200L,
                100L
        );

        lenient().when(authorRepository.findById(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(collectionService.createCollection(inputDto))
                .expectErrorMatches(throwable -> throwable instanceof DescriptionException
                        && throwable.getMessage().contains("Validation failed: Description 200-1000 chars"))
                .verify();
    }


    @Test
    void updateCollection_Success() {
        sampleCollection.setPrice(new BigDecimal("6.00"));
        sampleCollection.setDescription("A".repeat(250));

        UpdateCollectionDto updateDto = new UpdateCollectionDto("Updated Name", null, null, null, null);

        when(collectionRepository.findCollectionById(1L)).thenReturn(Mono.just(sampleCollection));
        when(collectionRepository.save(sampleCollection)).thenReturn(Mono.just(sampleCollection));
        when(collectionMapper.toGetDto(sampleCollection)).thenReturn(sampleGetDto);

        StepVerifier.create(collectionService.updateCollection(1L, updateDto))
                .expectNext(sampleGetDto)
                .verifyComplete();

        verify(collectionMapper).updateEntityFromDto(updateDto, sampleCollection);
    }

    @Test
    void updateCollection_InvalidDescriptionInEntity_ThrowsDescriptionException() {
        sampleCollection.setPrice(new BigDecimal("10.00"));
        sampleCollection.setDescription("Too short");

        UpdateCollectionDto updateDto = new UpdateCollectionDto("Updated Name", null, null, null, null);
        when(collectionRepository.findCollectionById(1L)).thenReturn(Mono.just(sampleCollection));

        StepVerifier.create(collectionService.updateCollection(1L, updateDto))
                .expectError(DescriptionException.class)
                .verify();

        verify(collectionRepository, never()).save(any());
    }


    @Test
    void deleteCollection_Success() {
        when(collectionRepository.deleteById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(collectionService.deleteCollection(1L))
                .expectNext(true)
                .verifyComplete();
    }


    @Test
    void findShortResponseById_Success() {
        ImageShortResponse mockImageResponse = new ImageShortResponse("/path", "img.png");
        CollectionShortResponse expectedShortResponse = new CollectionShortResponse(1L, "Java Bundle", mockImageResponse);

        when(collectionRepository.findById(1L)).thenReturn(Mono.just(sampleCollection));
        when(previewImageService.findShortResponseById(100L)).thenReturn(Mono.just(mockImageResponse));
        when(collectionMapper.mapToShortResponse(sampleCollection, mockImageResponse)).thenReturn(expectedShortResponse);

        StepVerifier.create(collectionService.findShortResponseById(1L))
                .expectNext(expectedShortResponse)
                .verifyComplete();
    }
}
