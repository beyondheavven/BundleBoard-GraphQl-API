package com.source.bundleboard.storage;


import reactor.core.publisher.Mono;

public interface SupabaseStorageService {

    Mono<Void> deleteFiles(String fileNames, String bucketName);

    Mono<Void> deleteFolder(String folderPath, String bucketName);

    Mono<String> getImageUrl(String fileName);

}
