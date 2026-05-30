package com.source.bundleboard.collection.controller;

import com.source.bundleboard.author.dto.AuthorResponse;
import com.source.bundleboard.author.dto.AuthorShortResponse;
import com.source.bundleboard.author.service.AuthorService;
import com.source.bundleboard.collection.dto.*;
import com.source.bundleboard.collection.service.CollectionService;
import com.source.bundleboard.image.dto.ImageShortResponse;
import com.source.bundleboard.image.service.PreviewImageService;
import com.source.bundleboard.mediaresource.dto.GetMediaResourceByIdResponse;
import com.source.bundleboard.mediaresource.service.MediaResourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    private final AuthorService authorService;

    private final PreviewImageService imageService;

    private final MediaResourceService mediaResourceService;

    @PreAuthorize("permitAll()")
    @QueryMapping
    public Flux<CollectionResponse> getAllCollections() {
        return collectionService.getAllCollections();
    }

    @PreAuthorize("permitAll()")
    @QueryMapping
    public Mono<GetCollectionByIdResponse> getCollectionById(@Argument Long id) {
        return collectionService.getCollectionById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR')")
    @MutationMapping
    public Mono<CreateCollectionResponse> createCollection(@Argument @Valid CreateNewCollectionInput input) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getName())
                .flatMap(username -> collectionService.createCollection(input, username));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR')")
    @MutationMapping
    public Mono<GetCollectionByIdResponse> updateCollection(@Argument Long id, @Argument(name = "input") UpdateCollectionDto collection) {
        return collectionService.updateCollection(id, collection);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR')")
    @MutationMapping
    public Mono<Boolean> deleteCollection(@Argument Long id) {
        return collectionService.deleteCollection(id);
    }

    @SchemaMapping(typeName = "CollectionResponse", field = "author")
    public Mono<AuthorShortResponse> getAuthor(CollectionResponse collection) {
        return authorService.findShortResponseById(collection.authorId());
    }

    @SchemaMapping(typeName = "CollectionResponse", field = "previewImage")
    public Mono<ImageShortResponse> getPreviewImage(CollectionResponse collection) {
        return imageService.findShortResponseById(collection.previewImageId());
    }

    @SchemaMapping(typeName = "GetCollectionByIdResponse", field = "author")
    public Mono<AuthorResponse> getFullAuthor(GetCollectionByIdResponse collection) {
        return authorService.findFullAuthorById(collection.authorId());
    }

    @SchemaMapping(typeName = "GetCollectionByIdResponse", field = "previewImage")
    public Mono<ImageShortResponse> getPreviewImageForDetails(GetCollectionByIdResponse collection) {
        return imageService.findShortResponseById(collection.previewImageId());
    }

    @SchemaMapping(typeName = "GetCollectionByIdResponse", field = "mediaResource")
    public Mono<GetMediaResourceByIdResponse> getMediaResourceDetails(GetCollectionByIdResponse collection){
        return mediaResourceService.findGetMediaResourceById(collection.mediaResourceId());
    }


}
