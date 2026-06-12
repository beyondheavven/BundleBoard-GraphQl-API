package com.source.bundleboard.comments.service;

import com.source.bundleboard.comments.dto.AddCommentRequest;
import com.source.bundleboard.comments.dto.CommentResponse;
import com.source.bundleboard.comments.model.Comment;
import com.source.bundleboard.comments.repository.CommentRepository;
import com.source.bundleboard.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final UserService userService;


    @Override
    public Flux<Comment> getCommentsByCollectionId(Long collectionId) {
        if (collectionId == null) {
            return Flux.empty();
        }
        return commentRepository.findAllByCollectionIdOrderByCreatedAtDesc(collectionId);
    }

    @Override
    public Mono<CommentResponse> addComment(AddCommentRequest input, UserDetails userDetails) {
        return userService.getUserByUsername(userDetails.getUsername())
                .flatMap(user -> {
                    Comment comment = new Comment();
                    comment.setCollectionId(input.collectionId());
                    comment.setUserId(user.getId());
                    comment.setContent(input.content());
                    comment.setCreatedAt(Instant.now());
                    comment.setUpdatedAt(Instant.now());

                    return commentRepository.save(comment)
                            .map(savedComment -> new CommentResponse(
                                    savedComment,
                                    true,
                                    "Comment added successfully"
                            ));
                });
    }

    public Mono<Boolean> deleteComment(Long commentId, UserDetails userDetails) {
        return userService.getUserByUsername(userDetails.getUsername())
                .flatMap(user -> commentRepository.findById(commentId)
                        .switchIfEmpty(Mono.error(new RuntimeException("Comment not found")))
                        .flatMap(comment -> {
                            if (comment.getUserId().equals(user.getId())) {
                                return commentRepository.delete(comment).thenReturn(true);
                            } else {
                                return Mono.error(new AccessDeniedException("You can only delete your own comments"));
                            }
                        })
                );
    }

    @Override
    public Flux<Comment> getCommentByUserId(Long userId) {
        if (userId == null) {
            return Flux.empty();
        }
        return commentRepository.findByUserId(userId);
    }
}
