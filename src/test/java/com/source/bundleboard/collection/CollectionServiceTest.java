package com.source.bundleboard.collection;

import com.source.bundleboard.api.exception.AuthorNotFoundException;
import com.source.bundleboard.api.exception.CollectionNotFoundException;
import com.source.bundleboard.api.exception.DescriptionException;
import com.source.bundleboard.author.model.Author;
import com.source.bundleboard.author.service.AuthorService;
import com.source.bundleboard.collection.dto.*;
import com.source.bundleboard.collection.mapper.CollectionMapper;
import com.source.bundleboard.collection.model.Collection;
import com.source.bundleboard.collection.repository.CollectionRepository;
import com.source.bundleboard.collection.service.CollectionServiceImpl;
import com.source.bundleboard.collectionImage.model.CollectionImage;
import com.source.bundleboard.collectionImage.repository.CollectionImageRepository;
import com.source.bundleboard.collectionTag.serivce.CollectionTagService;
import com.source.bundleboard.image.dto.ImageShortInput;
import com.source.bundleboard.image.model.PreviewImage;
import com.source.bundleboard.image.service.PreviewImageService;
import com.source.bundleboard.mediaresource.dto.MediaResourceInput;
import com.source.bundleboard.mediaresource.model.MediaResource;
import com.source.bundleboard.mediaresource.model.MimeType;
import com.source.bundleboard.mediaresource.model.Provider;
import com.source.bundleboard.mediaresource.repository.MediaResourceRepository;
import com.source.bundleboard.rabbitmq.dto.StorageTask;
import com.source.bundleboard.rabbitmq.producer.TaskProducer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    private CollectionTagService collectionTagService;

    @Mock
    private TaskProducer taskProducer;

    @Mock
    private CollectionImageRepository collectionImageRepository;

    @InjectMocks
    private CollectionServiceImpl collectionService;

    private Collection sampleCollection;
    private GetCollectionByIdResponse sampleGetDto;
    private Author sampleAuthor;
    private String username = "test_author";
    private String validDescription = "A".repeat(150);

    @BeforeEach
    void setUp() {
        sampleAuthor = new Author();
        sampleAuthor.setId(42L);

        sampleCollection = new Collection();
        sampleCollection.setId(1L);
        sampleCollection.setName("Java Bundle");
        sampleCollection.setDescription(validDescription);
        sampleCollection.setPrice(new BigDecimal("10.00"));
        sampleCollection.setMediaResourceId(200L);
        sampleCollection.setAuthorId(42L);

        sampleGetDto = new GetCollectionByIdResponse(
                1L,
                "Java Bundle",
                validDescription,
                new BigDecimal("10.00"),
                "http://tutorials.com/1",
                42L,
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
        int page = 0;
        int size = 9;
        Pageable expectedPageable = PageRequest.of(page, size, Sort.by("id").ascending());

        CollectionResponse responseDto = new CollectionResponse(
                1L,
                "Java Bundle",
                "Desc",
                new BigDecimal("10.00"),
                42L,
                null,
                0L,
                false
        );

        when(collectionRepository.findAllBy(expectedPageable)).thenReturn(Flux.just(sampleCollection));
        when(collectionMapper.toDto(sampleCollection)).thenReturn(responseDto);

        StepVerifier.create(collectionService.getAllCollections(page, size))
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

        CollectionImage mockRelation = new CollectionImage(1L, 1L, 100L);

        when(authorService.findByUsername(username)).thenReturn(Mono.just(sampleAuthor));
        when(mediaResourceRepository.save(any(MediaResource.class))).thenReturn(Mono.just(mockMedia));
        when(collectionRepository.save(any(Collection.class))).thenReturn(Mono.just(sampleCollection));

        when(previewImageService.saveAll(anyList())).thenReturn(Flux.just(mockImage));
        when(collectionImageRepository.saveAll(anyIterable())).thenReturn(Flux.just(mockRelation));
        when(collectionTagService.saveAll(anyList())).thenReturn(Flux.empty());

        StepVerifier.create(collectionService.createCollection(inputDto, username))
                .assertNext(response -> {
                    assertTrue(response.success());
                    assertEquals("Java Bundle", response.name());
                })
                .verifyComplete();
    }

    private @NotNull CreateNewCollectionInput getCreateNewCollectionInput() {
        MediaResourceInput mediaInput = new MediaResourceInput("file.zip", "/path", MimeType.zip, Provider.local, 1000L);
        ImageShortInput imageInput = new ImageShortInput("img.png", "/path/img", MimeType.png, 1920, 1080, 204857L);

        return new CreateNewCollectionInput(
                "New Pack",
                validDescription,
                new BigDecimal("15.00"),
                "http://url",
                List.of(1L, 2L),
                List.of(imageInput),
                mediaInput
        );
    }

    @Test
    void createCollection_AuthorNotFound_ThrowsAuthorNotFoundException() {
        CreateNewCollectionInput inputDto = new CreateNewCollectionInput(
                "Pack",
                validDescription,
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
    void updateCollection_Success_NoImagesProvided() {
        UpdateCollectionRequest updateDto = new UpdateCollectionRequest("Updated Name", validDescription, new BigDecimal("6.00"), List.of());

        when(collectionRepository.findCollectionById(1L)).thenReturn(Mono.just(sampleCollection));
        when(collectionRepository.save(sampleCollection)).thenReturn(Mono.just(sampleCollection));
        when(collectionMapper.toGetDto(sampleCollection)).thenReturn(sampleGetDto);

        StepVerifier.create(collectionService.updateCollection(1L, updateDto))
                .expectNext(sampleGetDto)
                .verifyComplete();

        verify(collectionMapper).updateEntityFromDto(updateDto, sampleCollection);
        verifyNoInteractions(previewImageService, collectionImageRepository, taskProducer);
    }

    @Test
    void updateCollection_InvalidDescription_ThrowsDescriptionException() {
        UpdateCollectionRequest updateDto = new UpdateCollectionRequest("Updated Name", "Too short", new BigDecimal("10.00"), null);

        when(collectionRepository.findCollectionById(1L)).thenReturn(Mono.just(sampleCollection));

        StepVerifier.create(collectionService.updateCollection(1L, updateDto))
                .expectError(DescriptionException.class)
                .verify();

        verify(collectionRepository, never()).save(any());
    }

    @Test
    void deleteCollection_Success() {
        String folderPath = "gradients/collection-12345";

        PreviewImage mockImage = new PreviewImage();
        mockImage.setId(10L);

        when(collectionRepository.findById(1L)).thenReturn(Mono.just(sampleCollection));

        when(collectionTagService.deleteAllByCollectionsId(1L)).thenReturn(Mono.empty());
        when(collectionImageRepository.deleteAllByCollectionId(1L)).thenReturn(Mono.empty());

        when(taskProducer.sendStorageTask(any(StorageTask.class))).thenReturn(Mono.empty());

        when(previewImageService.findAllByCollectionId(1L)).thenReturn(Flux.just(mockImage));
        when(collectionRepository.deleteById(1L)).thenReturn(Mono.empty());
        when(previewImageService.deleteById(10L)).thenReturn(Mono.empty());

        when(mediaResourceRepository.deleteById(200L)).thenReturn(Mono.empty());

        StepVerifier.create(collectionService.deleteCollection(1L, folderPath))
                .expectNext(true)
                .verifyComplete();

        verify(collectionTagService).deleteAllByCollectionsId(1L);
        verify(collectionImageRepository).deleteAllByCollectionId(1L);
        verify(collectionRepository).deleteById(1L);
        verify(taskProducer, times(2)).sendStorageTask(any(StorageTask.class));
    }

    @Test
    void deleteCollection_NotFound_ThrowsException() {
        when(collectionRepository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(collectionService.deleteCollection(1L, "path"))
                .expectError(CollectionNotFoundException.class)
                .verify();

        verifyNoInteractions(taskProducer, previewImageService, mediaResourceRepository, collectionTagService);
    }

    @Test
    void findShortResponseById_Success() {
        CollectionShortResponse expectedShortResponse = new CollectionShortResponse(1L, "Java Bundle", Collections.emptyList());

        when(collectionRepository.findById(1L)).thenReturn(Mono.just(sampleCollection));

        StepVerifier.create(collectionService.findShortResponseById(1L))
                .expectNext(expectedShortResponse)
                .verifyComplete();
    }

    @Test
    void findAllByAuthorId_Success() {
        CollectionRow dbRow = mock(CollectionRow.class);

        when(dbRow.id()).thenReturn(1L);
        when(dbRow.name()).thenReturn("Java Bundle");
        when(dbRow.price()).thenReturn(new BigDecimal("10.00"));
        when(dbRow.description()).thenReturn(validDescription);
        when(collectionRepository.findAllByAuthorId(42L)).thenReturn(Flux.just(dbRow));
        StepVerifier.create(collectionService.findAllByAuthorId(42L))
                .assertNext(response -> {
                    assertEquals(1L, response.id());
                    assertEquals("Java Bundle", response.name());
                    assertEquals(new BigDecimal("10.00"), response.price());
                })
                .verifyComplete();
    }
}