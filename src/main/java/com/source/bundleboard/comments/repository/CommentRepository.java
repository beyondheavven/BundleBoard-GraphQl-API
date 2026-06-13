package com.source.bundleboard.comments.repository;

import com.source.bundleboard.comments.model.Comment;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CommentRepository extends R2dbcRepository<Comment, Long> {

    Flux<Comment> findAllByCollectionIdOrderByCreatedAtDesc(Long collectionId);

    Flux<Comment> findByUserId(Long userId);
}
