package com.source.bundleboard.collection.controller;

import com.source.bundleboard.author.dto.AuthorResponse;
import com.source.bundleboard.author.dto.AuthorShortResponse;
import com.source.bundleboard.author.dto.BaseAuthorResponse;
import com.source.bundleboard.author.service.AuthorService;
import com.source.bundleboard.collection.dto.CollectionResponse;
import com.source.bundleboard.collection.dto.GetCollectionResponse;
import com.source.bundleboard.collection.dto.CreateNewCollectionDto;
import com.source.bundleboard.collection.dto.UpdateCollectionDto;
import com.source.bundleboard.collection.service.CollectionService;
import com.source.bundleboard.image.dto.BaseImageResponse;
import com.source.bundleboard.image.dto.ImageResponse;
import com.source.bundleboard.image.dto.ImageShortResponse;
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
    public Flux<CollectionResponse> getAllCollections() {
        return collectionService.getAllCollections();
    }

    @PreAuthorize("permitAll()")
    @QueryMapping
    public Mono<GetCollectionResponse> getCollectionById(@Argument Long id) {
        return collectionService.getCollectionById(id);
    }

    @PreAuthorize("hasAnyRole('admin', 'author')")
    @MutationMapping
    public Mono<GetCollectionResponse> createCollection(@Argument(name = "input") CreateNewCollectionDto collection) {
        return collectionService.createCollection(collection);
    }

    @PreAuthorize("hasAnyRole('admin', 'author')")
    @MutationMapping
    public Mono<GetCollectionResponse> updateCollection(@Argument Long id, @Argument(name = "input") UpdateCollectionDto collection) {
        return collectionService.updateCollection(id, collection);
    }

    @PreAuthorize("hasAnyRole('admin', 'author')")
    @MutationMapping
    public Mono<Boolean> deleteCollection(@Argument Long id) {
        return collectionService.deleteCollection(id);
    }


    @SchemaMapping(typeName = "Collection", field = "mediaResource")
    public Mono<MediaResourceResponseDto> getMediaResource(GetCollectionResponse collection){
        return mediaResourceService.findById(collection.mediaResourceId());
    }

    @SchemaMapping(typeName = "CollectionResponse", field = "author")
    public Mono<BaseAuthorResponse> getAuthorForResponse(CollectionResponse collection) {
        return authorService.findById(collection.authorId());
    }

    @SchemaMapping(typeName = "CollectionResponse", field = "previewImage")
    public Mono<BaseImageResponse> getPreviewImageForResponse(CollectionResponse collection) {
        return imageService.findByImageId(collection.previewImageId());
    }

    @SchemaMapping(typeName = "CollectionResponse", field = "author")
    public Mono<AuthorShortResponse> getAuthor(CollectionResponse collection) {
        return authorService.findShortResponseById(collection.authorId());
    }

    @SchemaMapping(typeName = "CollectionResponse", field = "previewImage")
    public Mono<ImageShortResponse> getPreviewImage(CollectionResponse collection) {
        return imageService.findShortResponseById(collection.previewImageId());
    }


}
