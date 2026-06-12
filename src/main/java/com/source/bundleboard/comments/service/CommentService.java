package com.source.bundleboard.comments.service;

import com.source.bundleboard.comments.dto.AddCommentRequest;
import com.source.bundleboard.comments.dto.CommentResponse;
import com.source.bundleboard.comments.model.Comment;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommentService {

    Flux<Comment> getCommentsByCollectionId(Long collectionId);

    Mono<CommentResponse> addComment(AddCommentRequest input, UserDetails userDetails);

    Mono<Boolean> deleteComment(Long commentId, UserDetails userDetails);
}
