package com.source.bundleboard.comments;

import com.source.bundleboard.comments.dto.AddCommentRequest;
import com.source.bundleboard.comments.dto.CommentResponse;
import com.source.bundleboard.comments.model.Comment;
import com.source.bundleboard.comments.repository.CommentRepository;
import com.source.bundleboard.comments.service.CommentServiceImpl;
import com.source.bundleboard.user.model.User;
import com.source.bundleboard.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.ArgumentMatchers.argThat;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User sampleUser;
    private Comment sampleComment;
    private final Long userId = 42L;
    private final Long collectionId = 100L;
    private final Long commentId = 1L;
    private final String username = "test_user";

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(userId);
        sampleUser.setUsername(username);

        sampleComment = new Comment();
        sampleComment.setId(commentId);
        sampleComment.setCollectionId(collectionId);
        sampleComment.setUserId(userId);
        sampleComment.setContent("Great bundle!");
        sampleComment.setCreatedAt(Instant.now());
        sampleComment.setUpdatedAt(Instant.now());
    }

    // --- getCommentsByCollectionId TESTS ---

    @Test
    void getCommentsByCollectionId_Success() {
        when(commentRepository.findAllByCollectionIdOrderByCreatedAtDesc(collectionId))
                .thenReturn(Flux.fromIterable(List.of(sampleComment)));

        StepVerifier.create(commentService.getCommentsByCollectionId(collectionId))
                .expectNext(sampleComment)
                .verifyComplete();
    }

    @Test
    void getCommentsByCollectionId_NullId_ReturnsEmptyFlux() {
        StepVerifier.create(commentService.getCommentsByCollectionId(null))
                .verifyComplete();

        verifyNoInteractions(commentRepository);
    }

    // --- addComment TESTS ---

    @Test
    void addComment_Success() {
        AddCommentRequest request = new AddCommentRequest(collectionId, "New comment!");

        when(userDetails.getUsername()).thenReturn(username);
        when(userService.getUserByUsername(username)).thenReturn(Mono.just(sampleUser));
        when(commentRepository.save(any(Comment.class))).thenReturn(Mono.just(sampleComment));

        StepVerifier.create(commentService.addComment(request, userDetails))
                .assertNext(response -> {
                    assertTrue(response.success());
                    assertEquals("Comment added successfully", response.message());
                    assertEquals(sampleComment.getId(), response.comment().getId());
                })
                .verifyComplete();

        verify(commentRepository).save(argThat(comment -> {
            assertEquals(collectionId, comment.getCollectionId());
            assertEquals(userId, comment.getUserId());
            assertEquals("New comment!", comment.getContent());
            return true;
        }));
    }

    // --- deleteComment TESTS ---

    @Test
    void deleteComment_Success_OwnComment() {
        when(userDetails.getUsername()).thenReturn(username);
        when(userService.getUserByUsername(username)).thenReturn(Mono.just(sampleUser));
        when(commentRepository.findById(commentId)).thenReturn(Mono.just(sampleComment));
        when(commentRepository.delete(sampleComment)).thenReturn(Mono.empty());

        StepVerifier.create(commentService.deleteComment(commentId, userDetails))
                .expectNext(true)
                .verifyComplete();

        verify(commentRepository).delete(sampleComment);
    }

    @Test
    void deleteComment_CommentNotFound_ThrowsException() {
        when(userDetails.getUsername()).thenReturn(username);
        when(userService.getUserByUsername(username)).thenReturn(Mono.just(sampleUser));
        when(commentRepository.findById(commentId)).thenReturn(Mono.empty());

        StepVerifier.create(commentService.deleteComment(commentId, userDetails))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Comment not found"))
                .verify();

        verify(commentRepository, never()).delete(any());
    }

    @Test
    void deleteComment_AccessDenied_NotOwnComment() {
        Comment otherUserComment = new Comment();
        otherUserComment.setId(commentId);
        otherUserComment.setUserId(99L);

        when(userDetails.getUsername()).thenReturn(username);
        when(userService.getUserByUsername(username)).thenReturn(Mono.just(sampleUser));
        when(commentRepository.findById(commentId)).thenReturn(Mono.just(otherUserComment));

        StepVerifier.create(commentService.deleteComment(commentId, userDetails))
                .expectErrorMatches(throwable -> throwable instanceof AccessDeniedException &&
                        throwable.getMessage().equals("You can only delete your own comments"))
                .verify();

        verify(commentRepository, never()).delete(any());
    }

    // --- getCommentByUserId TESTS ---

    @Test
    void getCommentByUserId_Success() {
        when(commentRepository.findByUserId(userId)).thenReturn(Flux.just(sampleComment));

        StepVerifier.create(commentService.getCommentByUserId(userId))
                .expectNext(sampleComment)
                .verifyComplete();
    }

    @Test
    void getCommentByUserId_NullId_ReturnsEmptyFlux() {
        StepVerifier.create(commentService.getCommentByUserId(null))
                .verifyComplete();

        verifyNoInteractions(commentRepository);
    }
}