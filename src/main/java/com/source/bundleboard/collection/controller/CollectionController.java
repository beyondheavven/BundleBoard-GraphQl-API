package com.source.bundleboard.collection.controller;

import com.source.bundleboard.author.dto.AuthorResponseDto;
import com.source.bundleboard.author.service.AuthorService;
import com.source.bundleboard.collection.dto.CollectionResponseDto;
import com.source.bundleboard.collection.dto.CreateNewCollectionDto;
import com.source.bundleboard.collection.dto.UpdateCollectionDto;
import com.source.bundleboard.collection.service.CollectionService;
import com.source.bundleboard.image.dto.PreviewImageResponseDto;
import com.source.bundleboard.image.service.PreviewImageService;
import com.source.bundleboard.mediaresource.dto.MediaResourceResponseDto;
import com.source.bundleboard.mediaresource.service.MediaResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    private final AuthorService authorService;

    private final PreviewImageService imageService;

    private final MediaResourceService mediaResourceService;

    @PreAuthorize("permitAll()")
    @QueryMapping
    public Flux<CollectionResponseDto> getAllCollections() {
        return collectionService.getAllCollections();
    }

    @PreAuthorize("permitAll()")
    @QueryMapping
    public Mono<CollectionResponseDto> getCollectionById(@Argument Long id) {
        return collectionService.getCollectionById(id);
    }

    @PreAuthorize("hasAnyRole('admin', 'author')")
    @MutationMapping
    public Mono<CollectionResponseDto> createCollection(@Argument(name = "input") CreateNewCollectionDto collection) {
        return collectionService.createCollection(collection);
    }

    @PreAuthorize("hasAnyRole('admin', 'author')")
    @MutationMapping
    public Mono<CollectionResponseDto> updateCollection(@Argument Long id, @Argument(name = "input") UpdateCollectionDto collection) {
        return collectionService.updateCollection(id, collection);
    }

    @PreAuthorize("hasAnyRole('admin', 'author')")
    @MutationMapping
    public Mono<Boolean> deleteCollection(@Argument Long id) {
        return collectionService.deleteCollection(id);
    }


    @SchemaMapping(typeName = "Collection", field = "author")
    public Mono<AuthorResponseDto> getAuthorFromResponse(CollectionResponseDto collection){
        return authorService.findById(collection.authorId());
    }

    @SchemaMapping(typeName = "Collection", field = "previewImage")
    public Mono<PreviewImageResponseDto> getPreviewImage(CollectionResponseDto collection){
        return imageService.findByImageId(collection.previewImageId());
    }

    @SchemaMapping(typeName = "Collection", field = "mediaResource")
    public Mono<MediaResourceResponseDto> getMediaResource(CollectionResponseDto collection){
        return mediaResourceService.findById(collection.mediaResourceId());
    }


}
