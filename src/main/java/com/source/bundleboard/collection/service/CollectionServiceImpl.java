package com.source.bundleboard.collection.service;

import com.source.bundleboard.api.exception.AuthorNotFoundException;
import com.source.bundleboard.api.exception.CollectionNotFoundException;
import com.source.bundleboard.api.exception.DescriptionException;
import com.source.bundleboard.api.exception.MinimalPriceException;
import com.source.bundleboard.author.service.AuthorService;
import com.source.bundleboard.collection.dto.*;
import com.source.bundleboard.collection.mapper.CollectionMapper;
import com.source.bundleboard.collection.model.Collection;
import com.source.bundleboard.collection.repository.CollectionRepository;
import com.source.bundleboard.image.dto.ImageShortResponse;
import com.source.bundleboard.image.model.PreviewImage;
import com.source.bundleboard.image.service.PreviewImageService;
import com.source.bundleboard.mediaresource.model.MediaFileType;
import com.source.bundleboard.mediaresource.model.MediaResource;
import com.source.bundleboard.mediaresource.repository.MediaResourceRepository;
import com.source.bundleboard.collectiontag.model.CollectionTag;
import com.source.bundleboard.collectiontag.repository.CollectionTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CollectionServiceImpl implements CollectionService {

    private final CollectionRepository collectionRepository;

    private final AuthorService authorService;

    private final CollectionMapper collectionMapper;

    private static final BigDecimal MIN_PRICE = new BigDecimal("5.00");

    private final PreviewImageService previewImageService;

    private final MediaResourceRepository mediaResourceRepository;

    private final CollectionTagRepository collectionTagRepository;

    @Override
    public Mono<GetCollectionByIdResponse> getCollectionById(Long id) {
        return collectionRepository.findCollectionById(id)
                .map(collectionMapper::toGetDto)
                .switchIfEmpty(Mono.error(new CollectionNotFoundException()));
    }

    @Override
    public Flux<CollectionResponse> getAllCollections(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
            return collectionRepository.findAllBy(pageable)
                .map(collectionMapper::toDto)
                .switchIfEmpty(Flux.error(new CollectionNotFoundException()));
    }

    @Transactional
    @Override
    public Mono<CreateCollectionResponse> createCollection(CreateNewCollectionInput input, String username) {
        return Mono.defer(() -> {
            if (input.price().compareTo(MIN_PRICE) < 0) {
                return Mono.error(new MinimalPriceException("Minimal price is 5 USD"));
            }

            return authorService.findByUsername(username)
                    .switchIfEmpty(Mono.error(new AuthorNotFoundException()))
                    .flatMap(author -> {

                        MediaResource mediaResource = new MediaResource();
                        mediaResource.setFileName(input.mediaResource().fileName());
                        mediaResource.setFilePath(input.mediaResource().filePath());
                        mediaResource.setMimeType(input.mediaResource().mimeType());
                        mediaResource.setProvider(input.mediaResource().provider());
                        mediaResource.setFileSize(input.mediaResource().fileSize());
                        mediaResource.setFileType(MediaFileType.archive);

                        return mediaResourceRepository.save(mediaResource)
                                .flatMap(savedResource -> {

                                    Collection collection = new Collection();
                                    collection.setName(input.name());
                                    collection.setDescription(input.description());
                                    collection.setPrice(input.price());
                                    collection.setVideoTutorialUrl(input.videoTutorialUrl());
                                    collection.setMediaResourceId(savedResource.getId());
                                    collection.setAuthorId(author.getId());

                                    return collectionRepository.save(collection)
                                            .flatMap(savedCollection -> {

                                                List<PreviewImage> newImages = input.galleryImages().stream()
                                                        .map(imgDto -> {
                                                            PreviewImage img = new PreviewImage();
                                                            img.setFileName(imgDto.fileName());
                                                            img.setFilePath(imgDto.filePath());
                                                            img.setMimeType(imgDto.mimeType());
                                                            img.setWidth(imgDto.width());
                                                            img.setHeight(imgDto.height());
                                                            img.setFileSize(imgDto.fileSize());
                                                            img.setCollectionsId(savedCollection.getId());
                                                            return img;
                                                        })
                                                        .toList();

                                                return previewImageService.saveAll(newImages)
                                                        .flatMap(savedImages -> {
                                                            if (savedImages.isEmpty()) {
                                                                return Mono.error(new DescriptionException("Gallery must contain at least one image"));
                                                            }

                                                            Long previewImageId = savedImages.get(0).getId();
                                                            savedCollection.setPreviewImageId(previewImageId);

                                                            List<CollectionTag> tagRelations = input.tagIds().stream()
                                                                    .map(tagId -> {
                                                                        CollectionTag relation = new CollectionTag();
                                                                        relation.setTagsId(tagId);
                                                                        relation.setCollectionsId(savedCollection.getId());
                                                                        return relation;
                                                                    })
                                                                    .toList();

                                                            return collectionRepository.save(savedCollection)
                                                                    .flatMap(updatedCollection ->
                                                                            collectionTagRepository.saveAll(tagRelations)
                                                                                    .then(Mono.fromSupplier(() -> {
                                                                                        CreateCollectionResponse response = new CreateCollectionResponse(
                                                                                                savedCollection.getId(),
                                                                                                savedCollection.getName(),
                                                                                                true
                                                                                        );
                                                                                        return response;
                                                                                    }))
                                                                    );
                                                        });
                                            });
                                });
                    });
        });
    }

    @Override
    public Mono<GetCollectionByIdResponse> updateCollection(Long id, UpdateCollectionDto collection) {
        return collectionRepository.findCollectionById(id)
                .switchIfEmpty(Mono.error(new CollectionNotFoundException()))
                .flatMap(entity -> {
                    if(entity.getPrice().compareTo(MIN_PRICE) < 0){
                        return Mono.error(new MinimalPriceException("Minimal price is 5 USD"));
                    }

                    if(entity.getDescription().length() < 200 || entity.getDescription().length() > 1000){
                        return Mono.error(new DescriptionException("Description must be between 200 and 1000 characters"));
                    }

                    collectionMapper.updateEntityFromDto(collection, entity);
                    return collectionRepository.save(entity);
                })
                .map(collectionMapper::toGetDto);
    }

    @Override
    public Mono<Boolean> deleteCollection(Long id) {
        return collectionRepository.deleteById(id).thenReturn(true).defaultIfEmpty(false);
    }

    @Override
    public Mono<CollectionShortResponse> findShortResponseById(Long collectionId) {
        return collectionRepository.findById(collectionId)
                .switchIfEmpty(Mono.error(new CollectionNotFoundException()))
                .flatMap(collection ->
                    previewImageService.findShortResponseById(collection.getPreviewImageId())
                            .map(imageDto -> collectionMapper.mapToShortResponse(collection, imageDto))
                );
    }

    @Override
    public Flux<AuthoredCollectionResponse> findAllByAuthorId(Long authorId) {
        return collectionRepository.findAllByAuthorId(authorId)
                .map(row -> new AuthoredCollectionResponse(
                        row.id(),
                        row.name(),
                        row.price(),
                        row.description(),
                        new ImageShortResponse(row.previewFilePath(), row.previewFileName())
                ));
    }


}
