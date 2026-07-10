package com.source.bundleboard.collectionLike.controller;

import com.source.bundleboard.collection.dto.AuthoredCollectionResponse;
import com.source.bundleboard.collection.dto.CollectionResponse;
import com.source.bundleboard.collection.dto.GetCollectionByIdResponse;
import com.source.bundleboard.collection.dto.GetCollectionBySlugResponse;
import com.source.bundleboard.collectionLike.service.CollectionLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class CollectionLikeController {

    private final CollectionLikeService collectionLikeService;


    @PreAuthorize("isAuthenticated()")
    @MutationMapping
    public Mono<Boolean> toggleFavorite(@Argument Long collectionId) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getName())
                .flatMap(username -> collectionLikeService.toggleFavorite(collectionId, username));
    }

    @SchemaMapping(typeName = "GetCollectionBySlugResponse", field = "likesCount")
    public Mono<Integer> getLikesCount(GetCollectionBySlugResponse collection) {
        return collectionLikeService.countByCollectionId(collection.id());
    }

    @SchemaMapping(typeName = "GetCollectionBySlugResponse", field = "isLiked")
    public Mono<Boolean> getIsLiked(GetCollectionBySlugResponse collection) {
        return collectionLikeService.isLikedByCurrentUser(collection.id());
    }

    @SchemaMapping(typeName = "CollectionResponse", field = "likesCount")
    public Mono<Integer> getLikesCount(CollectionResponse collection) {
        return collectionLikeService.countByCollectionId(collection.id());
    }

    @SchemaMapping(typeName = "GetCollectionByIdResponse", field = "likesCount")
    public Mono<Integer> getLikesCount(GetCollectionByIdResponse collection) {
        return collectionLikeService.countByCollectionId(collection.id());
    }

    @SchemaMapping(typeName = "AuthoredCollectionResponse", field = "likesCount")
    public Mono<Integer> getLikesCount(AuthoredCollectionResponse collection) {
        return collectionLikeService.countByCollectionId(collection.id());
    }

    @SchemaMapping(typeName = "GetCollectionByIdResponse", field = "isLiked")
    public Mono<Boolean> getIsLiked(GetCollectionByIdResponse collection) {
        return collectionLikeService.isLikedByCurrentUser(collection.id());
    }

    @SchemaMapping(typeName = "CollectionResponse", field = "isLiked")
    public Mono<Boolean> getIsLiked(CollectionResponse collection) {
        return collectionLikeService.isLikedByCurrentUser(collection.id());
    }

    @SchemaMapping(typeName = "AuthoredCollectionResponse", field = "isLiked")
    public Mono<Boolean> getIsLiked(AuthoredCollectionResponse collection) {
        return collectionLikeService.isLikedByCurrentUser(collection.id());
    }
}
