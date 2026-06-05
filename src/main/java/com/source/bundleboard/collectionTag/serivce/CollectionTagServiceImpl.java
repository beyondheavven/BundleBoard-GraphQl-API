package com.source.bundleboard.collectionTag.serivce;

import com.source.bundleboard.collectionTag.model.CollectionTag;
import com.source.bundleboard.collectionTag.repository.CollectionTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CollectionTagServiceImpl implements CollectionTagService {

    private final CollectionTagRepository collectionTagRepository;

    @Override
    public Flux<CollectionTag> saveAll(List<CollectionTag> tagRelations) {
        if (tagRelations == null || tagRelations.isEmpty()) {
            return Flux.empty();
        }
        return collectionTagRepository.saveAll(tagRelations);
    }

    @Override
    public Mono<Void> deleteAllByCollectionsId(Long collectionId) {
        return collectionTagRepository.deleteAllByCollectionsId(collectionId);
    }
}
