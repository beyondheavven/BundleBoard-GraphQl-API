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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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
}
