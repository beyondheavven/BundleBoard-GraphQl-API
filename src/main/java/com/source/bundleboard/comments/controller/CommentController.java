package com.source.bundleboard.comments.controller;

import com.source.bundleboard.collection.dto.CollectionCommentResponse;
import com.source.bundleboard.collection.service.CollectionService;
import com.source.bundleboard.comments.dto.AddCommentRequest;
import com.source.bundleboard.comments.dto.CommentResponse;
import com.source.bundleboard.comments.model.Comment;
import com.source.bundleboard.comments.service.CommentService;
import com.source.bundleboard.user.dto.UserCommentResponse;
import com.source.bundleboard.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    private final UserService userService;

    private final CollectionService collectionService;


    @QueryMapping
    @PreAuthorize("permitAll()")
    public Flux<Comment> getCommentsByCollectionId(@Argument Long collectionId){
        return commentService.getCommentsByCollectionId(collectionId);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Mono<CommentResponse> addComment(@Argument @Valid AddCommentRequest input, @AuthenticationPrincipal UserDetails userDetails){
        return commentService.addComment(input, userDetails);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Mono<Boolean> deleteComment(@Argument Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return commentService.deleteComment(id, userDetails);
    }

    @SchemaMapping(typeName = "Comment", field = "user")
    public Mono<UserCommentResponse> getUserForComment(Comment comment) {
        return userService.getUserCommentResponseById(comment.getUserId());
    }

    @SchemaMapping(typeName = "Comment", field = "collection")
    public Mono<CollectionCommentResponse> getCollectionForComment(Comment comment) {
        return collectionService.getCollectionCommentResponseById(comment.getCollectionId());
    }

}
