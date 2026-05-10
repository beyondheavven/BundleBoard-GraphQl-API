package com.source.bundleboard.api.storage;

import com.source.bundleboard.image.dto.BulkImageResponse;
import com.source.bundleboard.image.dto.UploadImageResponse;
import com.source.bundleboard.image.dto.UploadStatus;
import com.source.bundleboard.image.model.PreviewImage;
import com.source.bundleboard.image.service.PreviewImageService;
import com.source.bundleboard.mediaresource.model.MimeType;
import com.source.bundleboard.properties.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupabaseStorageServiceImpl implements SupabaseStorageService {

    private final S3AsyncClient s3AsyncClient;

    private final S3Properties s3Properties;

    private final PreviewImageService previewImageService;

    @Override
    public Mono<BulkImageResponse> uploadImages(Flux<FilePart> fileParts) {
        return fileParts
                .flatMap(filePart ->
                        uploadSingleImage(filePart)
                                .map(Optional::of)
                                .onErrorReturn(Optional.empty())
                )
                .collectList()
                .map(results -> {
                    List<UploadImageResponse> successfulImages = results.stream()
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .toList();

                    int totalRequested = results.size();
                    int successCount = successfulImages.size();

                    UploadStatus status = UploadStatus.SUCCESS;
                    if (successCount == 0 && totalRequested > 0) {
                        status = UploadStatus.FAILED;
                    } else if (successCount < totalRequested) {
                        status = UploadStatus.PARTLY_SUCCESS;
                    }

                    long totalSize = successfulImages.stream()
                            .mapToLong(UploadImageResponse::fileSize)
                            .sum();

                    return new BulkImageResponse(
                            successfulImages,
                            successCount,
                            totalSize,
                            status
                    );
                });
    }

    @Override
    public Mono<UploadImageResponse> uploadImage(FilePart filePart) {
        return uploadSingleImage(filePart);
    }

    @Override
    public Mono<Void> deleteFiles(String fileNames) {
        return null;
    }

    private Mono<UploadImageResponse> uploadSingleImage(FilePart filePart) {
        String fileName = filePart.filename();
        String extension = fileName.substring(fileName.lastIndexOf("."));
        String key = UUID.randomUUID() + extension;
        return filePart.content()
                .map(DataBuffer::asByteBuffer)
                .collectList()
                .flatMap(byteBuffers -> {
                    long size = byteBuffers.stream().mapToLong(ByteBuffer::remaining).sum();
                    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                            .bucket(s3Properties.getBucketName())
                            .key(key)
                            .contentType(filePart.headers().getContentType().toString())
                            .build();

                    return Mono.fromFuture(s3AsyncClient.putObject(putObjectRequest,
                            AsyncRequestBody.fromByteBuffers(byteBuffers.toArray(new ByteBuffer[0]))))
                            .flatMap(response -> {
                                PreviewImage image = new PreviewImage();
                                image.setFileName(fileName);
                                image.setFilePath(s3Properties.getPublicUrlPrefix() + key);
                                image.setMimeType(mapMimeType(filePart.headers().getContentType().toString()));
                                image.setFileSize(size);
                                image.setWidth(0);
                                image.setHeight(0);

                                return previewImageService.save(image);
                            })
                            .map(saved -> new UploadImageResponse(
                                    saved.getId(),
                                    saved.getFileName(),
                                    saved.getFilePath(),
                                    saved.getMimeType(),
                                    saved.getFileSize()
                            ));
                });
    }

    private MimeType mapMimeType(String contentType) {
        if (contentType == null) return MimeType.jpeg;
        if (contentType.contains("png")) return MimeType.png;
        if (contentType.contains("mp4")) return MimeType.mp4;
        if (contentType.contains("pdf")) return MimeType.pdf;
        if (contentType.contains("zip")) return MimeType.zip;
        if (contentType.contains("rar")) return MimeType.rar;
        return MimeType.jpeg;
    }
}
