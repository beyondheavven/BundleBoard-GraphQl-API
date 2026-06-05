package com.source.bundleboard.collectionTag.serivce;

import com.source.bundleboard.collectionTag.model.CollectionTag;
import net.logstash.logback.encoder.com.lmax.disruptor.dsl.EventHandlerGroup;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CollectionTagService {

    Flux<CollectionTag> saveAll(List<CollectionTag> tagRelations);

    Mono<Void> deleteAllByCollectionsId(Long collectionId);
}
