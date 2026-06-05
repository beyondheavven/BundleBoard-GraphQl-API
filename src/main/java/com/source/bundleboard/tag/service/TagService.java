package com.source.bundleboard.tag.service;


import com.source.bundleboard.tag.model.Tag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TagService {

    Mono<Tag> findById(Long id);

    Mono<Tag> findByName(String name);

    Flux<Tag> findAll();

    Flux<Tag> findAllById(List<Long> ids);

    Flux<Tag> findTagsByCollectionId(Long collectionId);
}
