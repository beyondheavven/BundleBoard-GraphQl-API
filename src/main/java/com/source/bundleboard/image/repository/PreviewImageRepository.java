package com.source.bundleboard.image.repository;

import com.source.bundleboard.image.model.PreviewImage;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PreviewImageRepository extends R2dbcRepository<PreviewImage, Long> {

    Mono<PreviewImage> findById(Long id);

    @Query("""
        SELECT i.* FROM images i 
        INNER JOIN collection_images ci ON i.id = ci.image_id 
        WHERE ci.collection_id = :collectionId
    """)
    Flux<PreviewImage> findAllByCollectionId(Long collectionId);
}
