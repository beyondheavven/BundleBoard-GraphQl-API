package com.source.bundleboard.tag.service;

import com.source.bundleboard.tag.model.Tag;
import com.source.bundleboard.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;


    @Override
    public Mono<Tag> findById(Long id) {
        if (id == null) {
            return Mono.empty();
        }
        return tagRepository.findById(id);
    }

    @Override
    public Mono<Tag> findByName(String name) {
        if (name == null || name.isBlank()) {
            return Mono.empty();
        }
        return tagRepository.findByName(name);
    }

    @Override
    public Flux<Tag> findAll() {
        return tagRepository.findAll();
    }

    @Override
    public Flux<Tag> findAllById(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Flux.empty();
        }
        return tagRepository.findAllById(ids);
    }

    @Override
    public Flux<Tag> findTagsByCollectionId(Long collectionId) {
        if (collectionId == null) {
            return Flux.empty();
        }
        return tagRepository.findByCollectionId(collectionId);
    }
}
