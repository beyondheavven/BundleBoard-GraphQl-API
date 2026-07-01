package com.source.bundleboard.collectionLike.service;

import com.source.bundleboard.author.service.AuthorService;
import com.source.bundleboard.collectionLike.model.CollectionLike;
import com.source.bundleboard.collectionLike.repository.CollectionLikeRepository;
import com.source.bundleboard.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CollectionLikeServiceImpl implements CollectionLikeService {

    private final CollectionLikeRepository collectionLikeRepository;

    private final UserService userService;


    @Transactional
    public Mono<Boolean> toggleFavorite(Long collectionId, String username) {
        return userService.findByUsername(username)
                .flatMap(user ->
                        collectionLikeRepository.findByCollectionIdAndUserId(collectionId, user.getId())
                                .flatMap(existingLike ->
                                        collectionLikeRepository.delete(existingLike).thenReturn(false)
                                )
                                .switchIfEmpty(Mono.defer(() ->
                                        collectionLikeRepository.save(new CollectionLike(null, collectionId, user.getId(), Instant.now())).thenReturn(true)
                                ))
                );
    }

    @Override
    public Mono<Integer> countByCollectionId(Long id) {
        if (id == null) {
            return Mono.empty();
        }
        return collectionLikeRepository.countByCollectionId(id);
    }

    @Override
    public Mono<Boolean> isLikedByCurrentUser(Long collectionId) {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(context -> Mono.justOrEmpty(context.getAuthentication()))
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getName)
                .flatMap(userService::getUserByUsername)
                .flatMap(user -> collectionLikeRepository.existsByCollectionIdAndUserId(collectionId, user.getId()))
                .defaultIfEmpty(false);
    }
}
