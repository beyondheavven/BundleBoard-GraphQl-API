package com.source.bundleboard.collectionImage.repository;

import com.source.bundleboard.collectionImage.model.CollectionImage;
import net.logstash.logback.encoder.com.lmax.disruptor.EventHandler;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CollectionImageRepository extends R2dbcRepository<CollectionImage, Long> {

    Mono<Void> deleteAllByCollectionId(Long collectionId);
}
