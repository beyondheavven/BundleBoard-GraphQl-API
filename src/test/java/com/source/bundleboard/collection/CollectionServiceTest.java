package com.source.bundleboard.collection;

import com.source.bundleboard.api.exception.AuthorNotFoundException;
import com.source.bundleboard.api.exception.CollectionNotFoundException;
import com.source.bundleboard.api.exception.DescriptionException;
import com.source.bundleboard.api.exception.MinimalPriceException;
import com.source.bundleboard.author.model.Author;
import com.source.bundleboard.author.service.AuthorService;
import com.source.bundleboard.collection.dto.*;
import com.source.bundleboard.collection.mapper.CollectionMapper;
import com.source.bundleboard.collection.model.Collection;
import com.source.bundleboard.collection.repository.CollectionRepository;
import com.source.bundleboard.collection.service.CollectionServiceImpl;
import com.source.bundleboard.image.dto.ImageShortInput;
import com.source.bundleboard.image.dto.ImageShortResponse;
import com.source.bundleboard.image.model.PreviewImage;
import com.source.bundleboard.image.service.PreviewImageService;
import com.source.bundleboard.mediaresource.dto.MediaResourceInput;
import com.source.bundleboard.mediaresource.model.MediaResource;
import com.source.bundleboard.mediaresource.model.MimeType;
import com.source.bundleboard.mediaresource.model.Provider;
import com.source.bundleboard.mediaresource.repository.MediaResourceRepository;
import com.source.bundleboard.collectiontag.repository.CollectionTagRepository;
import org.jetbrains.annotations.NotNull;
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
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CollectionServiceTest {

    @Mock
    private CollectionRepository collectionRepository;

    @Mock
    private AuthorService authorService;

    @Mock
    private CollectionMapper collectionMapper;

    @Mock
    private PreviewImageService previewImageService;

    @Mock
    private MediaResourceRepository mediaResourceRepository;

    @Mock
    private CollectionTagRepository collectionTagRepository;

    @InjectMocks
    private CollectionServiceImpl collectionService;

    private Collection sampleCollection;
    private GetCollectionByIdResponse sampleGetDto;
    private Author sampleAuthor;
    private String username = "test_author";



    @BeforeEach
    void setUp() {
        sampleAuthor = new Author();
        sampleAuthor.setId(42L);

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
        CreateNewCollectionInput inputDto = getCreateNewCollectionInput();

        MediaResource mockMedia = new MediaResource();
        mockMedia.setId(200L);

        PreviewImage mockImage = new PreviewImage();
        mockImage.setId(100L);

        when(authorService.findByUsername(username)).thenReturn(Mono.just(sampleAuthor));
        when(mediaResourceRepository.save(any(MediaResource.class))).thenReturn(Mono.just(mockMedia));
        when(collectionRepository.save(any(Collection.class))).thenReturn(Mono.just(sampleCollection));
        when(previewImageService.saveAll(anyList())).thenReturn(Mono.just(List.of(mockImage)));
        when(collectionTagRepository.saveAll(anyList())).thenReturn(Flux.empty());

        StepVerifier.create(collectionService.createCollection(inputDto, username))
                .assertNext(response -> {
                    org.junit.jupiter.api.Assertions.assertTrue(response.success());
                    org.junit.jupiter.api.Assertions.assertEquals("Java Bundle", response.name());
                })
                .verifyComplete();
    }

    private static @NotNull CreateNewCollectionInput getCreateNewCollectionInput() {
        MediaResourceInput mediaInput = new MediaResourceInput("file.zip", "/path", MimeType.zip, Provider.local, 1000L);
        ImageShortInput imageInput = new ImageShortInput("img.png", "/path/img", MimeType.png, 1920, 1080, 204857L);

        CreateNewCollectionInput inputDto = new CreateNewCollectionInput(
                "New Pack",
                "Valid description",
                new BigDecimal("15.00"),
                "http://url",
                List.of(1L, 2L),
                List.of(imageInput),
                mediaInput
        );
        return inputDto;
    }

    @Test
    void createCollection_LowPrice_ThrowsMinimalPriceException() {
        CreateNewCollectionInput inputDto = new CreateNewCollectionInput(
                "Cheap Pack",
                "Desc",
                new BigDecimal("4.99"),
                null,
                List.of(),
                List.of(),
                null
        );

        StepVerifier.create(collectionService.createCollection(inputDto, username))
                .expectErrorMatches(throwable -> throwable instanceof MinimalPriceException
                        && throwable.getMessage().contains("Minimal price is 5 USD"))
                .verify();

        verifyNoInteractions(authorService, collectionRepository, mediaResourceRepository, previewImageService);
    }

    @Test
    void createCollection_AuthorNotFound_ThrowsAuthorNotFoundException() {
        CreateNewCollectionInput inputDto = new CreateNewCollectionInput(
                "Pack",
                "Desc",
                new BigDecimal("10.00"),
                null,
                List.of(),
                List.of(),
                null
        );

        when(authorService.findByUsername(username)).thenReturn(Mono.empty());

        StepVerifier.create(collectionService.createCollection(inputDto, username))
                .expectError(AuthorNotFoundException.class)
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
