package com.source.bundleboard.storage;

import com.source.bundleboard.config.properties.S3Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class SupabaseStorageServiceTest {

    @Mock
    private S3AsyncClient s3AsyncClient;

    @Mock
    private S3Properties s3Properties;

    @InjectMocks
    private SupabaseStorageServiceImpl storageService;

    private final String bucket = "my-bucket";

    @BeforeEach
    void setUp() {
        lenient().when(s3Properties.getPreviewsPublicUrlPrefix()).thenReturn("https://cdn.example.com/");
    }

    @Test
    void deleteFiles_Success() {
        String files = "key1, key2";
        when(s3AsyncClient.deleteObject(any(DeleteObjectRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(DeleteObjectResponse.builder().build()));

        StepVerifier.create(storageService.deleteFiles(files, bucket))
                .verifyComplete();

        verify(s3AsyncClient, times(2)).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void deleteFiles_EmptyInput_ReturnsEmpty() {
        StepVerifier.create(storageService.deleteFiles("", bucket))
                .verifyComplete();

        verifyNoInteractions(s3AsyncClient);
    }

    @Test
    void deleteFolder_Success() {
        String folder = "images";
        String bucket = "my-bucket";

        S3Object obj1 = S3Object.builder().key("images/1.jpg").build();
        S3Object obj2 = S3Object.builder().key("images/2.jpg").build();
        ListObjectsV2Response listResponse = ListObjectsV2Response.builder()
                .contents(List.of(obj1, obj2))
                .build();

        when(s3AsyncClient.listObjectsV2(any(ListObjectsV2Request.class)))
                .thenReturn(CompletableFuture.completedFuture(listResponse));

        when(s3AsyncClient.deleteObject(any(DeleteObjectRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(DeleteObjectResponse.builder().build()));

        StepVerifier.create(storageService.deleteFolder(folder, bucket))
                .verifyComplete();

        verify(s3AsyncClient).listObjectsV2(any(ListObjectsV2Request.class));
        verify(s3AsyncClient, times(2)).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void getImageUrl_ReturnsPrefixedUrl() {
        String fileName = "my-image.png";

        StepVerifier.create(storageService.getImageUrl(fileName))
                .expectNext("https://cdn.example.com/my-image.png")
                .verifyComplete();
    }
}