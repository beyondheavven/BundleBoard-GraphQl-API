package com.source.bundleboard.storage;

import com.source.bundleboard.config.properties.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupabaseStorageServiceImpl implements SupabaseStorageService {

    private final S3AsyncClient s3AsyncClient;
    private final S3Properties s3Properties;

    @Override
    public Mono<Void> deleteFiles(String fileNames, String bucketName) {
        if (fileNames == null || fileNames.isBlank()) {
            return Mono.empty();
        }

        List<String> keysToDelete = java.util.Arrays.stream(fileNames.split(","))
                .map(String::trim)
                .filter(name -> !name.isBlank())
                .map(this::extractS3Key)
                .toList();

        return Flux.fromIterable(keysToDelete)
                .flatMap(key -> {
                    DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build();

                    return Mono.fromFuture(s3AsyncClient.deleteObject(deleteRequest))
                            .doOnSuccess(response -> System.out.println("S3_NODE_PURGE_SUCCESS: " + bucketName + "/" + key))
                            .onErrorResume(e -> {
                                System.err.println("S3_NODE_PURGE_FAILURE for key: " + key + " | Error: " + e.getMessage());
                                return Mono.empty();
                            });
                })
                .then();
    }

    @Override
    public Mono<Void> deleteFolder(String folderPath, String bucketName) {
        if (folderPath == null || folderPath.isBlank()) {
            return Mono.empty();
        }

        String prefix = folderPath.endsWith("/") ? folderPath : folderPath + "/";

        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .build();

        return Mono.fromFuture(() -> s3AsyncClient.listObjectsV2(listRequest))
                .flatMapMany(response -> Flux.fromIterable(response.contents()))
                .map(software.amazon.awssdk.services.s3.model.S3Object::key)
                .flatMap(key -> {
                    DeleteObjectRequest deleteReq = DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build();

                    return Mono.fromFuture(() -> s3AsyncClient.deleteObject(deleteReq))
                            .doOnSuccess(res -> System.out.println("S3_FOLDER_PURGE_SUCCESS: " + bucketName + "/" + key))
                            .onErrorResume(e -> {
                                System.err.println("S3_FOLDER_PURGE_FAILURE for key: " + key + " | Error: " + e.getMessage());
                                return Mono.empty();
                            });
                })
                .then();
    }

    @Override
    public Mono<String> getImageUrl(String fileName) {
        return Mono.just(s3Properties.getPreviewsPublicUrlPrefix() + fileName);
    }

    private String extractS3Key(String fileNameOrUrl) {
        String prefix = s3Properties.getPreviewsPublicUrlPrefix();

        if (fileNameOrUrl != null && fileNameOrUrl.startsWith(prefix)) {
            return fileNameOrUrl.substring(prefix.length());
        }
        if (fileNameOrUrl != null && fileNameOrUrl.contains("showcase/")) {
            return fileNameOrUrl.substring(fileNameOrUrl.indexOf("showcase/"));
        }

        return fileNameOrUrl;
    }
}