package com.source.bundleboard.collectiontag.serivce;

import com.source.bundleboard.collectiontag.model.CollectionTag;
import net.logstash.logback.encoder.com.lmax.disruptor.dsl.EventHandlerGroup;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CollectionTagService {

    Mono<Void> deleteAllByCollectionsId(Long collectionsId);

    Mono<Void> saveAll(List<CollectionTag> tagRelations);
}
