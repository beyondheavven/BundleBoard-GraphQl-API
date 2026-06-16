package com.source.bundleboard.image;

import com.source.bundleboard.api.exception.ImageNotFoundException;
import com.source.bundleboard.image.dto.BaseImageResponse;
import com.source.bundleboard.image.dto.ImageShortResponse;
import com.source.bundleboard.image.mapper.PreviewImageMapper;
import com.source.bundleboard.image.model.PreviewImage;
import com.source.bundleboard.image.repository.PreviewImageRepository;
import com.source.bundleboard.image.service.PreviewImageServiceImpl;
import com.source.bundleboard.mediaresource.model.MimeType;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class PreviewImageServiceTest {

    @Mock
    private PreviewImageRepository previewImageRepository;

    @Mock
    private PreviewImageMapper previewImageMapper;

    @InjectMocks
    private PreviewImageServiceImpl previewImageService;

    private PreviewImage sampleImage;

    @BeforeEach
    void setUp() {
        sampleImage = new PreviewImage();
        sampleImage.setId(100L);
        sampleImage.setFileName("preview_cat.png");
        sampleImage.setFilePath("/uploads/images/preview_cat.png");
    }

    // --- findByImageId TESTS ---

    @Test
    void findByImageId_Success() {
        BaseImageResponse mockResponse = new BaseImageResponse(
                100L,
                "preview_cat.png",
                "/uploads/images/preview_cat.png",
                MimeType.png,
                1920,
                1080,
                512400L
        );

        when(previewImageRepository.findById(100L)).thenReturn(Mono.just(sampleImage));
        when(previewImageMapper.toDto(sampleImage)).thenReturn(mockResponse);

        StepVerifier.create(previewImageService.findByImageId(100L))
                .expectNext(mockResponse)
                .verifyComplete();

        verify(previewImageRepository).findById(100L);
        verify(previewImageMapper).toDto(sampleImage);
    }

    @Test
    void findByImageId_NotFound_ThrowsImageNotFoundException() {
        when(previewImageRepository.findById(100L)).thenReturn(Mono.empty());

        StepVerifier.create(previewImageService.findByImageId(100L))
                .expectError(ImageNotFoundException.class)
                .verify();

        verify(previewImageRepository).findById(100L);
        verifyNoInteractions(previewImageMapper);
    }

    // --- save TESTS ---

    @Test
    void save_Success() {
        when(previewImageRepository.save(any(PreviewImage.class))).thenReturn(Mono.just(sampleImage));

        StepVerifier.create(previewImageService.save(sampleImage))
                .expectNext(sampleImage)
                .verifyComplete();

        verify(previewImageRepository).save(sampleImage);
    }

    @Test
    void save_EmptyResult_ThrowsImageNotFoundException() {
        when(previewImageRepository.save(any(PreviewImage.class))).thenReturn(Mono.empty());

        StepVerifier.create(previewImageService.save(sampleImage))
                .expectError(ImageNotFoundException.class)
                .verify();
    }

    // --- findShortResponseById TESTS ---

    @Test
    void findShortResponseById_Success() {
        ImageShortResponse mockShortResponse = new ImageShortResponse(
                "/uploads/images/preview_cat.png",
                "preview_cat.png"
        );

        when(previewImageRepository.findById(100L)).thenReturn(Mono.just(sampleImage));
        when(previewImageMapper.toShortDto(sampleImage)).thenReturn(mockShortResponse);

        StepVerifier.create(previewImageService.findShortResponseById(100L))
                .assertNext(response -> {
                    assertEquals("preview_cat.png", response.fileName());
                    assertEquals("/uploads/images/preview_cat.png", response.filePath());
                })
                .verifyComplete();

        verify(previewImageRepository).findById(100L);
        verify(previewImageMapper).toShortDto(sampleImage);
    }

    @Test
    void findShortResponseById_NotFound_ThrowsImageNotFoundException() {
        when(previewImageRepository.findById(100L)).thenReturn(Mono.empty());

        StepVerifier.create(previewImageService.findShortResponseById(100L))
                .expectError(ImageNotFoundException.class)
                .verify();
    }

    // --- saveAll TESTS ---

    @Test
    void saveAll_Success() {
        List<PreviewImage> images = List.of(sampleImage);
        when(previewImageRepository.saveAll(images)).thenReturn(Flux.fromIterable(images));

        StepVerifier.create(previewImageService.saveAll(images))
                .expectNext(sampleImage)
                .verifyComplete();

        verify(previewImageRepository).saveAll(images);
    }

    @Test
    void saveAll_EmptyOrNullList_ReturnsEmptyFlux() {
        StepVerifier.create(previewImageService.saveAll(Collections.emptyList()))
                .verifyComplete();

        StepVerifier.create(previewImageService.saveAll(null))
                .verifyComplete();

        verifyNoInteractions(previewImageRepository);
    }

    // --- findAllByCollectionId TESTS ---

    @Test
    void findAllByCollectionId_Success() {
        Long collectionId = 1L;
        when(previewImageRepository.findAllByCollectionId(collectionId)).thenReturn(Flux.just(sampleImage));

        StepVerifier.create(previewImageService.findAllByCollectionId(collectionId))
                .expectNext(sampleImage)
                .verifyComplete();
    }

    @Test
    void findAllByCollectionId_NullId_ReturnsEmptyFlux() {
        StepVerifier.create(previewImageService.findAllByCollectionId(null))
                .verifyComplete();

        verifyNoInteractions(previewImageRepository);
    }

    // --- deleteById TESTS ---

    @Test
    void deleteById_Success() {
        when(previewImageRepository.deleteById(100L)).thenReturn(Mono.empty());

        StepVerifier.create(previewImageService.deleteById(100L))
                .verifyComplete();

        verify(previewImageRepository).deleteById(100L);
    }

    @Test
    void deleteById_NullId_ReturnsEmptyMono() {
        StepVerifier.create(previewImageService.deleteById(null))
                .verifyComplete();

        verifyNoInteractions(previewImageRepository);
    }

    // --- findAllShortResponsesByCollectionId TESTS ---

    @Test
    void findAllShortResponsesByCollectionId_Success() {
        Long collectionId = 1L;
        when(previewImageRepository.findAllByCollectionId(collectionId)).thenReturn(Flux.just(sampleImage));

        StepVerifier.create(previewImageService.findAllShortResponsesByCollectionId(collectionId))
                .assertNext(response -> {
                    assertEquals(sampleImage.getFilePath(), response.filePath());
                    assertEquals(sampleImage.getFileName(), response.fileName());
                })
                .verifyComplete();
    }

    @Test
    void findAllShortResponsesByCollectionId_NullId_ReturnsEmptyFlux() {
        StepVerifier.create(previewImageService.findAllShortResponsesByCollectionId(null))
                .verifyComplete();

        verifyNoInteractions(previewImageRepository);
    }

    // --- findByFilePath TESTS ---

    @Test
    void findByFilePath_Success() {
        String filePath = "/uploads/images/preview_cat.png";
        when(previewImageRepository.findByFilePath(filePath)).thenReturn(Flux.just(sampleImage));

        StepVerifier.create(previewImageService.findByFilePath(filePath))
                .expectNext(sampleImage)
                .verifyComplete();
    }

    @Test
    void findByFilePath_NullOrBlank_ReturnsEmptyFlux() {
        StepVerifier.create(previewImageService.findByFilePath(null))
                .verifyComplete();

        StepVerifier.create(previewImageService.findByFilePath("   "))
                .verifyComplete();

        verifyNoInteractions(previewImageRepository);
    }
}