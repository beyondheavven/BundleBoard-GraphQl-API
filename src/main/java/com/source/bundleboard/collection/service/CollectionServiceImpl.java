package com.source.bundleboard.collection.service;

import com.source.bundleboard.api.exception.AuthorNotFoundException;
import com.source.bundleboard.api.exception.CollectionNotFoundException;
import com.source.bundleboard.api.exception.DescriptionException;
import com.source.bundleboard.author.model.Author;
import com.source.bundleboard.author.service.AuthorService;
import com.source.bundleboard.collection.dto.*;
import com.source.bundleboard.collection.mapper.CollectionMapper;
import com.source.bundleboard.collection.model.Collection;
import com.source.bundleboard.collection.repository.CollectionRepository;
import com.source.bundleboard.collectionImage.model.CollectionImage;
import com.source.bundleboard.collectionImage.repository.CollectionImageRepository;
import com.source.bundleboard.collectionTag.serivce.CollectionTagService;
import com.source.bundleboard.image.model.PreviewImage;
import com.source.bundleboard.image.service.PreviewImageService;
import com.source.bundleboard.mediaresource.model.MediaFileType;
import com.source.bundleboard.mediaresource.model.MediaResource;
import com.source.bundleboard.mediaresource.model.MimeType;
import com.source.bundleboard.mediaresource.repository.MediaResourceRepository;
import com.source.bundleboard.collectionTag.model.CollectionTag;
import com.source.bundleboard.rabbitmq.dto.StorageOperationType;
import com.source.bundleboard.rabbitmq.dto.StorageTask;
import com.source.bundleboard.rabbitmq.producer.TaskProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CollectionServiceImpl implements CollectionService {

    private final CollectionRepository collectionRepository;

    private final AuthorService authorService;

    private final CollectionMapper collectionMapper;

    private final PreviewImageService previewImageService;

    private final MediaResourceRepository mediaResourceRepository;

    private final CollectionTagService collectionTagService;

    private final TaskProducer taskProducer;

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
                .map(collectionMapper::toDto);
    }

    @Transactional
    @Override
    public Mono<CreateCollectionResponse> createCollection(CreateNewCollectionInput input, String username) {
        return Mono.defer(() -> {
            if (input.mediaResource() == null && (input.externalLink() == null || input.externalLink().isBlank())) {
                return Mono.error(new DescriptionException("You must provide either a project file or an external link"));
            }

            return authorService.findByUsername(username)
                    .switchIfEmpty(Mono.error(new AuthorNotFoundException()))
                    .flatMap(author -> {

                        Mono<Long> processMediaMono = Mono.justOrEmpty(input.mediaResource())
                                .flatMap(mrInput -> {
                                    MediaResource mediaResource = new MediaResource();
                                    mediaResource.setFileName(mrInput.fileName());
                                    mediaResource.setFilePath(mrInput.filePath());
                                    mediaResource.setMimeType(mrInput.mimeType());
                                    mediaResource.setProvider(mrInput.provider());
                                    mediaResource.setFileSize(mrInput.fileSize());
                                    mediaResource.setFileType(MediaFileType.archive);

                                    return mediaResourceRepository.save(mediaResource).map(MediaResource::getId);
                                });

                        return processMediaMono.map(Optional::of).defaultIfEmpty(Optional.empty())
                                .flatMap(optMediaId -> {

                                    Collection collection = getCollection(input, author);
                                    optMediaId.ifPresent(collection::setMediaResourceId);
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
                                                                                                .thenReturn(new CreateCollectionResponse(
                                                                                                        savedCollection.getId(),
                                                                                                        savedCollection.getName(),
                                                                                                        true
                                                                                                ))
                                                                                );
                                                                    });
                                                        });
                                            });
                                });
                    });
        });
    }

    private static Collection getCollection(CreateNewCollectionInput input, Author author) {
        Collection collection = new Collection();
        collection.setName(input.name());
        collection.setDescription(input.description());
        collection.setPrice(input.price());
        collection.setVideoTutorialUrl(input.videoTutorialUrl());
        collection.setAuthorId(author.getId());
        collection.setExternalLink(input.externalLink());
        return collection;
    }

    @Transactional
    @Override
    public Mono<GetCollectionByIdResponse> updateCollection(Long id, UpdateCollectionRequest collection) {
        return collectionRepository.findCollectionById(id)
                .switchIfEmpty(Mono.error(new CollectionNotFoundException()))
                .flatMap(entity -> {

                    if(collection.description().length() < 100 || collection.description().length() > 2000){
                        return Mono.error(new DescriptionException("Description must be between 100 and 2000 characters"));
                    }
                    collectionMapper.updateEntityFromDto(collection, entity);
                    return collectionRepository.save(entity)
                            .flatMap(savedCollection -> {
                                if (collection.galleryImages() == null || collection.galleryImages().isEmpty()) {
                                    return Mono.just(savedCollection);
                                }

                                return previewImageService.findAllByCollectionId(id).collectList()
                                        .flatMap(oldImages -> {

                                            List<String> newPaths = collection.galleryImages().stream()
                                                    .map(PreviewImage::getFilePath)
                                                    .toList();

                                            List<PreviewImage> orphanedImages = oldImages.stream()
                                                    .filter(old -> !newPaths.contains(old.getFilePath()))
                                                    .toList();

                                            String orphanedPaths = orphanedImages.stream()
                                                    .map(PreviewImage::getFilePath)
                                                    .collect(Collectors.joining(","));

                                            Mono<Void> purgeStorage = orphanedPaths.isEmpty() ? Mono.empty() :
                                                    taskProducer.sendStorageTask(new StorageTask(StorageOperationType.DELETE_FILES, orphanedPaths, "previews"));

                                            Mono<Void> purgeDb = Flux.fromIterable(orphanedImages)
                                                    .flatMap(img -> previewImageService.deleteById(img.getId()))
                                                    .then();

                                            Mono<Void> clearOldLinks = collectionImageRepository.deleteAllByCollectionId(id);

                                            Mono<List<CollectionImage>> processNewImages = Flux.fromIterable(collection.galleryImages())
                                                    .concatMap(imgDto -> previewImageService.findByFilePath(imgDto.getFilePath())
                                                            .switchIfEmpty(Mono.defer(() -> {
                                                                PreviewImage newImg = new PreviewImage();
                                                                newImg.setFilePath(imgDto.getFilePath());
                                                                newImg.setFileName(imgDto.getFileName() != null ? imgDto.getFileName() : "update-image.webp");
                                                                newImg.setMimeType(imgDto.getMimeType() != null ? imgDto.getMimeType() : MimeType.webp);
                                                                newImg.setFileSize(imgDto.getFileSize() != null ? imgDto.getFileSize() : 0L);
                                                                newImg.setWidth(imgDto.getWidth() != null ? imgDto.getWidth() : 1200);
                                                                newImg.setHeight(imgDto.getHeight() != null ? imgDto.getHeight() : 800);
                                                                return previewImageService.save(newImg);
                                                            }))
                                                    )
                                                    .map(savedImg -> new CollectionImage(null, id, savedImg.getId()))
                                                    .collectList()
                                                    .flatMap(links -> collectionImageRepository.saveAll(links).collectList());

                                            return clearOldLinks
                                                    .then(purgeDb)
                                                    .then(purgeStorage)
                                                    .then(processNewImages)
                                                    .thenReturn(savedCollection);
                                        });
                            });
                })
                .map(collectionMapper::toGetDto);
    }

    @Override
    public Flux<CollectionResponse> getLikedCollections() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())
                .flatMapMany(username ->
                        authorService.findByUsername(username)
                                .flatMapMany(author -> this.findLikedCollectionsByAuthorId(author.getId()))
                );
    }

    @Override
    public Flux<CollectionResponse> findLikedCollectionsByAuthorId(Long authorId) {
        return collectionRepository.findLikedCollectionsByAuthorId(authorId)
                .map(collectionMapper::toDto);
    }

    @Override
    public Mono<CollectionShortResponse> findShortById(Long collectionId) {
        return collectionRepository.findById(collectionId)
                .map(collection -> new CollectionShortResponse(
                        collection.getId(),
                        collection.getName(),
                        null
                ));
    }

    @Override
    public Flux<CollectionResponse> searchByName(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return collectionRepository.findByNameContainingIgnoreCase(query, pageable)
                .map(collection -> new CollectionResponse(
                        collection.getId(),
                        collection.getName(),
                        collection.getDescription(),
                        collection.getPrice(),
                        collection.getExternalLink(),
                        collection.getAuthorId(),
                        null,
                        null,
                        false
                ));
    }

    @Override
    public Flux<CollectionResponse> getTopLikedCollections(int limit) {
        return collectionRepository.findTopLikedCollections(limit)
                .map(collectionMapper::toDto);
    }

    @Override
    public Mono<CollectionCommentResponse> getCollectionCommentResponseById(Long collectionId) {
        if (collectionId == null) {
            return Mono.error(new CollectionNotFoundException());
        }
        return collectionRepository.findById(collectionId)
                .map(collection -> new CollectionCommentResponse(
                        collection.getId(),
                        collection.getName()
                ));
    }

    @Override
    public Flux<CollectionResponse> getLatestCollections(Integer limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "id"));
        return collectionRepository.findAllBy(pageable).map(collectionMapper::toDto);
    }

    @Override
    public Flux<CollectionResponse> getSortedCollections(int page, int size, String sortBy, List<String> mimeTypeStrings){
        int offset = page * size;
        List<MimeType> mimeTypes = null;

        if (mimeTypeStrings != null && !mimeTypeStrings.isEmpty()) {
            mimeTypes = mimeTypeStrings.stream()
                    .map(String::toLowerCase)
                    .map(MimeType::valueOf)
                    .toList();
        }

        boolean hasFilter = mimeTypes != null && !mimeTypes.isEmpty();
        String sortKey = sortBy != null ? sortBy.toUpperCase() : "LATEST";

        Flux<Collection> collectionsFlux = switch (sortKey) {

            case "SIZE_ASC", "LIGHTEST" -> hasFilter
                    ? collectionRepository.findFilteredByMimeTypesSortedBySizeAsc(mimeTypes, size, offset)
                    : collectionRepository.findAllSortedBySizeAsc(size, offset);

            case "AUTHOR_SALES", "TOP_CREATORS" -> hasFilter
                    ? collectionRepository.findFilteredByMimeTypesSortedByAuthorSales(mimeTypes, size, offset)
                    : collectionRepository.findAllSortedByAuthorSales(size, offset);

            case "LIKES", "TOP_RATED" -> hasFilter
                    ? collectionRepository.findFilteredByMimeTypesSortedByLikes(mimeTypes, size, offset)
                    : collectionRepository.findAllSortedByLikes(size, offset);

            case "OLDEST", "DATE_ASC" -> hasFilter
                    ? collectionRepository.findFilteredByMimeTypesSortedByOldest(mimeTypes, size, offset)
                    : collectionRepository.findAllSortedByOldest(size, offset);

            case "ALPHABETICAL", "NAME_ASC" -> hasFilter
                    ? collectionRepository.findFilteredByMimeTypesSortedByAlphabetical(mimeTypes, size, offset)
                    : collectionRepository.findAllSortedByAlphabetical(size, offset);

            case "LATEST", "NEWEST", "LATEST_INIT" -> hasFilter
                    ? collectionRepository.findFilteredByMimeTypesSortedByLatest(mimeTypes, size, offset)
                    : collectionRepository.findAllSortedByLatest(size, offset);

            default -> hasFilter
                    ? collectionRepository.findFilteredByMimeTypesSortedByLatest(mimeTypes, size, offset)
                    : collectionRepository.findAllSortedByLatest(size, offset);
        };

        return collectionsFlux.map(collectionMapper::toDto);
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
                            ? taskProducer.sendStorageTask(new StorageTask(StorageOperationType.DELETE_FOLDERS, folderPath, "previews"))
                            .then(taskProducer.sendStorageTask(new StorageTask(StorageOperationType.DELETE_FOLDERS, folderPath, "vault")))
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
                        collection.getName(),
                        Collections.emptyList()
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