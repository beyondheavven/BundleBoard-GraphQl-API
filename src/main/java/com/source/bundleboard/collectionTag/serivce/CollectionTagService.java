package com.source.bundleboard.collectionTag.serivce;

import com.source.bundleboard.collectionTag.model.CollectionTag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CollectionTagService {

    Flux<CollectionTag> saveAll(List<CollectionTag> tagRelations);
}
