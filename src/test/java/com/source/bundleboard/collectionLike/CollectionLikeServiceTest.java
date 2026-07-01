package com.source.bundleboard.collectionLike;

import com.source.bundleboard.user.model.User;
import com.source.bundleboard.user.service.UserService;
import com.source.bundleboard.collectionLike.model.CollectionLike;
import com.source.bundleboard.collectionLike.repository.CollectionLikeRepository;
import com.source.bundleboard.collectionLike.service.CollectionLikeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class CollectionLikeServiceTest {

    @Mock
    private CollectionLikeRepository collectionLikeRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CollectionLikeServiceImpl collectionLikeService;

    private User testUser;
    private CollectionLike testLike;
    private final Long collectionId = 1L;
    private final String username = "test_user";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(42L);

        testLike = new CollectionLike(1L, collectionId, 42L, Instant.now());
    }


    @Test
    void toggleFavorite_Like_Success() {
        when(userService.findByUsername(anyString())).thenReturn(Mono.just(testUser));
        when(collectionLikeRepository.findByCollectionIdAndUserId(eq(collectionId), eq(42L))).thenReturn(Mono.empty());
        when(collectionLikeRepository.save(any(CollectionLike.class))).thenReturn(Mono.just(testLike));

        StepVerifier.create(collectionLikeService.toggleFavorite(collectionId, username))
                .expectNext(true)
                .verifyComplete();

        verify(collectionLikeRepository).save(any(CollectionLike.class));
    }

    @Test
    void toggleFavorite_Unlike_Success() {
        when(userService.findByUsername(anyString())).thenReturn(Mono.just(testUser));
        when(collectionLikeRepository.findByCollectionIdAndUserId(eq(collectionId), eq(42L))).thenReturn(Mono.just(testLike));
        when(collectionLikeRepository.delete(any(CollectionLike.class))).thenReturn(Mono.empty());

        StepVerifier.create(collectionLikeService.toggleFavorite(collectionId, username))
                .expectNext(false)
                .verifyComplete();

        verify(collectionLikeRepository).delete(any(CollectionLike.class));
    }


    @Test
    void countByCollectionId_Success() {
        when(collectionLikeRepository.countByCollectionId(collectionId)).thenReturn(Mono.just(10));

        StepVerifier.create(collectionLikeService.countByCollectionId(collectionId))
                .expectNext(10)
                .verifyComplete();
    }

    @Test
    void countByCollectionId_NullId_ReturnsEmpty() {
        StepVerifier.create(collectionLikeService.countByCollectionId(null))
                .verifyComplete();
        verifyNoInteractions(collectionLikeRepository);
    }


    @Test
    void isLikedByCurrentUser_Authenticated_ReturnsTrue() {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn(username);

        when(userService.findByUsername(username)).thenReturn(Mono.just(testUser));
        when(collectionLikeRepository.existsByCollectionIdAndUserId(collectionId, 42L)).thenReturn(Mono.just(true));

        StepVerifier.create(collectionLikeService.isLikedByCurrentUser(collectionId)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void isLikedByCurrentUser_Unauthenticated_ReturnsFalse() {
        StepVerifier.create(collectionLikeService.isLikedByCurrentUser(collectionId))
                .expectNext(false)
                .verifyComplete();

        verifyNoInteractions(userService, collectionLikeRepository);
    }
}