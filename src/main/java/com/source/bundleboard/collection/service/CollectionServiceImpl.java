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
import com.source.bundleboard.collectionImage.model.CollectionImage;
import com.source.bundleboard.collectionImage.repository.CollectionImageRepository;
import com.source.bundleboard.collectionTag.serivce.CollectionTagService;
import com.source.bundleboard.image.dto.ImageShortResponse;
import com.source.bundleboard.image.model.PreviewImage;
import com.source.bundleboard.image.service.PreviewImageService;
import com.source.bundleboard.mediaresource.model.MediaFileType;
import com.source.bundleboard.mediaresource.model.MediaResource;
import com.source.bundleboard.mediaresource.repository.MediaResourceRepository;
import com.source.bundleboard.collectionTag.model.CollectionTag;
import com.source.bundleboard.storage.SupabaseStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CollectionServiceImpl implements CollectionService {

    private final CollectionRepository collectionRepository;

    private final AuthorService authorService;

    private final CollectionMapper collectionMapper;

    private static final BigDecimal MIN_PRICE = new BigDecimal("5.00");

    private final PreviewImageService previewImageService;

    private final MediaResourceRepository mediaResourceRepository;

    private final CollectionTagService collectionTagService;

    private final SupabaseStorageService supabaseStorageService;

    private final CollectionImageRepository collectionImageRepository;

    @Override
    public Mono<GetCollectionByIdResponse> getCollectionById(Long id) {
        return collectionRepository.findCollectionById(id)
                .map(collectionMapper::toGetDto)
                .switchIfEmpty(Mono.error(new CollectionNotFoundException()));
    }

    @Override
    public Flux<CollectionResponse> getAllCollections(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
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
                                                            return img;
                                                        })
                                                        .toList();

                                                return previewImageService.saveAll(newImages).collectList()
                                                        .flatMap(savedImages -> {
                                                            if (savedImages.isEmpty()) {
                                                                return Mono.error(new DescriptionException("Gallery must contain at least one image"));
                                                            }

                                                            List<CollectionImage> imageRelations = savedImages.stream()
                                                                    .map(img -> new CollectionImage(null, savedCollection.getId(), img.getId()))
                                                                    .toList();

                                                            return collectionImageRepository.saveAll(imageRelations).collectList()
                                                                    .flatMap(savedImageRels -> {
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
                                                                                        collectionTagService.saveAll(tagRelations).collectList()
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
        });
    }

    @Override
    public Mono<GetCollectionByIdResponse> updateCollection(Long id, UpdateCollectionRequest collection) {
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

    @Transactional
    @Override
    public Mono<Boolean> deleteCollection(Long id, String folderPath) {
        return collectionRepository.findById(id)
                .switchIfEmpty(Mono.error(new CollectionNotFoundException()))
                .flatMap(collection -> {
                    Long mediaResourceId = collection.getMediaResourceId();

                    Mono<Void> databaseCleanup = collectionTagService.deleteAllByCollectionsId(id)
                            .then(collectionImageRepository.deleteAllByCollectionId(id))
                            .then(collectionRepository.deleteById(id))
                            .then(previewImageService.findAllByCollectionId(id).collectList()
                                    .flatMap(images -> Flux.fromIterable(images)
                                            .flatMap(img -> previewImageService.deleteById(img.getId()))
                                            .then()))
                            .then(Mono.justOrEmpty(mediaResourceId)
                                    .flatMap(mediaResourceRepository::deleteById)
                                    .then());

                    Mono<Void> storageCleanup = (folderPath != null && !folderPath.isBlank())
                            ? supabaseStorageService.deleteFolder(folderPath, "previews")
                            .then(supabaseStorageService.deleteFolder(folderPath, "vault"))
                            : Mono.empty();

                    return databaseCleanup
                            .then(storageCleanup)
                            .thenReturn(true);
                })
                .defaultIfEmpty(false);
    }

    @Override
    public Mono<CollectionShortResponse> findShortResponseById(Long collectionId) {
        return collectionRepository.findById(collectionId)
                .switchIfEmpty(Mono.error(new CollectionNotFoundException()))
                .map(collection -> new CollectionShortResponse(
                        collection.getId(),
                        collection.getName()
                ));
    }

    @Override
    public Flux<AuthoredCollectionResponse> findAllByAuthorId(Long authorId) {
        return collectionRepository.findAllByAuthorId(authorId)
                .map(row -> new AuthoredCollectionResponse(
                        row.id(),
                        row.name(),
                        row.price(),
                        row.description()
                ));
    }

    @Override
    public Mono<CollectionByTagResponse> getCollectionByTagName(CollectionFilterInput input) {
        return Mono.defer(() -> {
            int offset = input.page() * input.size();
            String cleanTagName = input.tagName().trim();
            Mono<List<CollectionResponse>> collectionMono = collectionRepository
                    .findCollectionsByTagNamePaged(cleanTagName, input.size(), offset)
                    .map(collectionMapper::toDto)
                    .collectList();

            Mono<Long> totalElementsMono = collectionRepository.countCollectionsByTagName(cleanTagName);

            return Mono.zip(collectionMono, totalElementsMono)
                    .map(tuple -> {
                        List<CollectionResponse> collections = tuple.getT1();
                        long totalElements = tuple.getT2();
                        int totalPages = (int) Math.ceil((double) totalElements / input.size());
                        if (totalPages == 0) totalPages = 1;
                        return new CollectionByTagResponse(collections, totalPages, totalElements);
                    })
                    .defaultIfEmpty(new CollectionByTagResponse(List.of(), 1, 0L));
        });
    }

    @Override
    public Flux<Collection> findAllByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Flux.empty();
        }
        return collectionRepository.findAllById(ids)
                .collectList()
                .flatMapMany(collections -> {
                    if(collections.size() != ids.size()){
                        log.warn("Only {} collections found out of {} requested", collections.size(), ids.size());
                    }
                    return Flux.fromIterable(collections);
                });
    }

    @Override
    public Mono<Collection> findById(Long collectionId) {
        return collectionRepository.findById(collectionId).switchIfEmpty(Mono.empty());
    }


}
