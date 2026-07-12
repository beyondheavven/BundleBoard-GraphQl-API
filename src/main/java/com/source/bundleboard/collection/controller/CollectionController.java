package com.source.bundleboard.collection.controller;

import com.source.bundleboard.author.dto.AuthorResponse;
import com.source.bundleboard.author.dto.AuthorShortResponse;
import com.source.bundleboard.author.service.AuthorService;
import com.source.bundleboard.collection.dto.*;
import com.source.bundleboard.collection.model.Collection;
import com.source.bundleboard.collection.service.CollectionService;
import com.source.bundleboard.image.dto.ImageShortResponse;
import com.source.bundleboard.image.service.PreviewImageService;
import com.source.bundleboard.mediaresource.dto.GetMediaResourceByIdResponse;
import com.source.bundleboard.mediaresource.service.MediaResourceService;
import com.source.bundleboard.purchase.model.PurchaseStatus;
import com.source.bundleboard.purchase.service.PurchaseService;
import com.source.bundleboard.tag.model.Tag;
import com.source.bundleboard.tag.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    private final AuthorService authorService;

    private final PreviewImageService imageService;

    private final MediaResourceService mediaResourceService;

    private final PurchaseService purchaseService;

    private final TagService tagService;

    // =========================================================
    // QUERIES
    // =========================================================

    @PreAuthorize("permitAll()")
    @QueryMapping
    public Flux<CollectionResponse> getAllCollections(@Argument int page, @Argument int size) {
        return collectionService.getAllCollections(page, size);
    }

    @PreAuthorize("permitAll()")
    @QueryMapping
    public Mono<GetCollectionByIdResponse> getCollectionById(@Argument Long id) {
        return collectionService.getCollectionById(id);
    }

    @PreAuthorize("permitAll()")
    @QueryMapping
    public Mono<GetCollectionBySlugResponse> getCollectionBySlug(@Argument String username, @Argument String slug) {
        return collectionService.getCollectionBySlug(username, slug);
    }

    @PreAuthorize("permitAll()")
    @QueryMapping
    public Mono<CollectionByTagResponse> getCollectionsByTag(@Argument @Valid CollectionFilterInput input) {
        return collectionService.getCollectionByTagName(input);
    }

    @PreAuthorize("permitAll()")
    @QueryMapping
    public Flux<CollectionResponse> getTopLikedCollections(@Argument Integer limit) {
        int actualLimit = (limit != null && limit > 0) ? limit : 10;
        return collectionService.getTopLikedCollections(actualLimit);
    }

    @PreAuthorize("isAuthenticated()")
    @QueryMapping
    public Flux<CollectionResponse> getLikedCollections() {
        return collectionService.getLikedCollections();
    }

    @PreAuthorize("permitAll()")
    @QueryMapping
    public Flux<CollectionResponse> getLatestCollections(@Argument Integer limit) {
        int actualLimit = (limit != null && limit > 0) ? limit : 10;
        return collectionService.getLatestCollections(actualLimit);
    }

    @PreAuthorize("permitAll()")
    @QueryMapping
    public Flux<CollectionResponse> getSortedCollections(@Argument int page, @Argument int size, @Argument String sortBy, @Argument List<String> mimeTypes) {
        return collectionService.getSortedCollections(page, size, sortBy, mimeTypes);
    }

    @PreAuthorize("permitAll()")
    @QueryMapping
    public Flux<CollectionResponse> getRandomCollections(@Argument int limit) {
        return collectionService.getRandomCollections(limit);
    }

    @PreAuthorize("permitAll()")
    @QueryMapping
    public Flux<CollectionResponse> searchCollections(@Argument String query, @Argument int size, @Argument int page) {
        if (query == null || query.trim().length() < 2) {
            return Flux.empty();
        }
        return collectionService.searchByName(query, page, size);
    }

    // =========================================================
    // MUTATIONS
    // =========================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR')")
    @MutationMapping
    public Mono<CreateCollectionResponse> createCollection(@Argument @Valid CreateNewCollectionInput input) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getName())
                .flatMap(username -> collectionService.createCollection(input, username));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR')")
    @MutationMapping
    public Mono<GetCollectionByIdResponse> updateCollection(@Argument Long id, @Argument(name = "input") UpdateCollectionRequest collection) {
        return collectionService.updateCollection(id, collection);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR')")
    @MutationMapping
    public Mono<Boolean> deleteCollection(@Argument Long id, @Argument String folderPath) {
        return collectionService.deleteCollection(id, folderPath);
    }

    // =========================================================
    // FIELD RESOLVERS — author
    // =========================================================

    @SchemaMapping(typeName = "CollectionResponse", field = "author")
    public Mono<AuthorShortResponse> getAuthor(CollectionResponse collection) {
        return authorService.findShortResponseById(collection.authorId());
    }

    @SchemaMapping(typeName = "AuthoredCollectionResponse", field = "author")
    public Mono<AuthorShortResponse> getAuthorForAuthoredCollection(AuthoredCollectionResponse collection) {
        log.info(">>> DEBUG authorId for collection {} = {}", collection.id(), collection.authorId());
        return authorService.findShortResponseById(collection.authorId());
    }

    @SchemaMapping(typeName = "GetCollectionByIdResponse", field = "author")
    public Mono<AuthorResponse> getFullAuthor(GetCollectionByIdResponse collection) {
        return authorService.findFullAuthorById(collection.authorId());
    }

    @SchemaMapping(typeName = "GetCollectionBySlugResponse", field = "author")
    public Mono<AuthorResponse> getAuthorForSlug(GetCollectionBySlugResponse collection) {
        return authorService.findFullAuthorById(collection.authorId());
    }

    @SchemaMapping(typeName = "Collection", field = "author")
    public Mono<AuthorResponse> getAuthor(Collection collection) {
        return authorService.findFullAuthorById(collection.getAuthorId());
    }

    @SchemaMapping(typeName = "AuthorResponse", field = "downloadCount")
    public Mono<Long> getAuthorDownloadCount(AuthorResponse author) {
        return purchaseService.countByAuthorIdAndStatus(author.id(), PurchaseStatus.succeeded)
                .defaultIfEmpty(0L);
    }

    // =========================================================
    // FIELD RESOLVERS — galleryImages
    // =========================================================

    @SchemaMapping(typeName = "CollectionResponse", field = "galleryImages")
    public Flux<ImageShortResponse> getGalleryImages(CollectionResponse collection) {
        return imageService.findAllShortResponsesByCollectionId(collection.id());
    }

    @SchemaMapping(typeName = "GetCollectionByIdResponse", field = "galleryImages")
    public Flux<ImageShortResponse> getGalleryImagesForDetails(GetCollectionByIdResponse collection) {
        return imageService.findAllShortResponsesByCollectionId(collection.id());
    }

    @SchemaMapping(typeName = "GetCollectionBySlugResponse", field = "galleryImages")
    public Flux<ImageShortResponse> getGalleryImagesForSlug(GetCollectionBySlugResponse collection) {
        return imageService.findAllShortResponsesByCollectionId(collection.id());
    }

    @SchemaMapping(typeName = "Collection", field = "galleryImages")
    public Flux<ImageShortResponse> getGalleryImagesForCollection(Collection collection) {
        return imageService.findAllShortResponsesByCollectionId(collection.getId());
    }

    @SchemaMapping(typeName = "AuthoredCollectionResponse", field = "galleryImages")
    public Flux<ImageShortResponse> getGalleryImagesForAuthored(AuthoredCollectionResponse collection) {
        return imageService.findAllShortResponsesByCollectionId(collection.id());
    }

    @SchemaMapping(typeName = "CollectionShortResponse", field = "galleryImages")
    public Flux<ImageShortResponse> getGalleryImagesForShort(CollectionShortResponse collection) {
        return imageService.findAllShortResponsesByCollectionId(collection.id());
    }

    // =========================================================
    // FIELD RESOLVERS — mediaResource
    // =========================================================

    @SchemaMapping(typeName = "GetCollectionByIdResponse", field = "mediaResource")
    public Mono<GetMediaResourceByIdResponse> getMediaResourceDetails(GetCollectionByIdResponse collection) {
        if (collection.mediaResourceId() == null) {
            return Mono.empty();
        }
        return mediaResourceService.findGetMediaResourceById(collection.mediaResourceId());
    }

    @SchemaMapping(typeName = "GetCollectionBySlugResponse", field = "mediaResource")
    public Mono<GetMediaResourceByIdResponse> getMediaResourceForSlug(GetCollectionBySlugResponse collection) {
        if (collection.mediaResourceId() == null) {
            return Mono.empty();
        }
        return mediaResourceService.findGetMediaResourceById(collection.mediaResourceId());
    }

    @SchemaMapping(typeName = "Collection", field = "mediaResource")
    public Mono<GetMediaResourceByIdResponse> getMediaResource(Collection collection) {
        if (collection.getMediaResourceId() == null) {
            return Mono.empty();
        }
        return mediaResourceService.findGetMediaResourceById(collection.getMediaResourceId());
    }

    // =========================================================
    // FIELD RESOLVERS — misc (downloadCount, tags)
    // =========================================================

    @SchemaMapping(typeName = "AuthoredCollectionResponse", field = "downloadCount")
    public Mono<Long> getCollectionDownloadCount(AuthoredCollectionResponse collection) {
        return purchaseService.countByCollectionIdAndStatus(collection.id(), PurchaseStatus.succeeded)
                .defaultIfEmpty(0L);
    }

    @SchemaMapping(typeName = "AuthoredCollectionResponse", field = "tags")
    public Flux<Tag> getTagsForAuthoredCollection(AuthoredCollectionResponse collection) {
        return tagService.findAllTagsByCollectionId(collection.id());
    }
}