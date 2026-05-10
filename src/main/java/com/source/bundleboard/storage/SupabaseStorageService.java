package com.source.bundleboard.storage;

import com.source.bundleboard.image.dto.BulkImageResponse;
import com.source.bundleboard.image.dto.UploadImageResponse;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SupabaseStorageService {

    Mono<BulkImageResponse> uploadImages(Flux<FilePart> fileParts);

    Mono<UploadImageResponse> uploadImage(FilePart filePart);

    Mono<Void> deleteFiles(String fileNames);
}
