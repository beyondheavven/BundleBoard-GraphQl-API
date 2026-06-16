package com.source.bundleboard.mediaresources;

import com.source.bundleboard.api.exception.MediaResourceNotFoundException;
import com.source.bundleboard.mediaresource.dto.GetMediaResourceByIdResponse;
import com.source.bundleboard.mediaresource.mapper.MediaResourceMapper;
import com.source.bundleboard.mediaresource.model.MediaFileType;
import com.source.bundleboard.mediaresource.model.MediaResource;
import com.source.bundleboard.mediaresource.model.MimeType;
import com.source.bundleboard.mediaresource.model.Provider;
import com.source.bundleboard.mediaresource.repository.MediaResourceRepository;
import com.source.bundleboard.mediaresource.service.MediaResourceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class MediaResourceServiceTest {

    @Mock
    private MediaResourceRepository mediaResourceRepository;

    @Mock
    private MediaResourceMapper mediaResourceMapper;

    @InjectMocks
    private MediaResourceServiceImpl mediaResourceService;

    private MediaResource sampleMediaResource;
    private GetMediaResourceByIdResponse expectedResponse;

    @BeforeEach
    void setUp() {
        MimeType mockMimeType = MimeType.png;
        MediaFileType mockFileType = MediaFileType.image;
        Provider mockProvider = Provider.google_drive;

        sampleMediaResource = new MediaResource(
                1L,
                "avatar.png",
                "/uploads/images/avatar.png",
                mockFileType,
                mockMimeType,
                mockProvider,
                1024L
        );

        expectedResponse = new GetMediaResourceByIdResponse(
                1L,
                "avatar.png",
                1024L,
                mockMimeType,
                mockProvider
        );
    }

    // --- findGetMediaResourceById TESTS ---

    @Test
    void findGetMediaResourceById_Success_ReturnsResponseDto() {
        Long resourceId = 1L;

        when(mediaResourceRepository.findById(resourceId)).thenReturn(Mono.just(sampleMediaResource));
        when(mediaResourceMapper.toDto(sampleMediaResource)).thenReturn(expectedResponse);

        Mono<GetMediaResourceByIdResponse> result = mediaResourceService.findGetMediaResourceById(resourceId);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals(1L, response.id());
                    assertEquals("avatar.png", response.fileName());
                    assertEquals(1024L, response.fileSize());
                    assertEquals(expectedResponse.mimeType(), response.mimeType());
                    assertEquals(expectedResponse.provider(), response.provider());
                })
                .verifyComplete();

        verify(mediaResourceRepository, times(1)).findById(resourceId);
        verify(mediaResourceMapper, times(1)).toDto(sampleMediaResource);
    }

    @Test
    void findGetMediaResourceById_NotFound_ThrowsMediaResourceNotFoundException() {
        Long resourceId = 999L;

        when(mediaResourceRepository.findById(resourceId)).thenReturn(Mono.empty());

        Mono<GetMediaResourceByIdResponse> result = mediaResourceService.findGetMediaResourceById(resourceId);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof MediaResourceNotFoundException
                        && throwable.getMessage().contains(String.valueOf(resourceId)))
                .verify();

        verify(mediaResourceRepository, times(1)).findById(resourceId);
        verifyNoInteractions(mediaResourceMapper);
    }

    // --- findById TESTS ---

    @Test
    void findById_Success() {
        Long resourceId = 1L;
        when(mediaResourceRepository.findById(resourceId)).thenReturn(Mono.just(sampleMediaResource));

        StepVerifier.create(mediaResourceService.findById(resourceId))
                .expectNext(sampleMediaResource)
                .verifyComplete();

        verify(mediaResourceRepository).findById(resourceId);
    }

    @Test
    void findById_NotFound_ReturnsEmptyMono() {
        Long resourceId = 999L;
        when(mediaResourceRepository.findById(resourceId)).thenReturn(Mono.empty());

        StepVerifier.create(mediaResourceService.findById(resourceId))
                .verifyComplete();

        verify(mediaResourceRepository).findById(resourceId);
    }

    @Test
    void findById_NullId_ReturnsEmptyMono() {
        StepVerifier.create(mediaResourceService.findById(null))
                .verifyComplete();

        verifyNoInteractions(mediaResourceRepository);
    }
}