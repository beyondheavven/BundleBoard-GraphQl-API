package com.source.bundleboard.collectiontag.serivce;

import com.source.bundleboard.collectiontag.model.CollectionTag;
import com.source.bundleboard.collectiontag.repository.CollectionTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CollectionTagServiceImpl implements CollectionTagService {

    private final CollectionTagRepository collectionTagRepository;

    @Override
    public Mono<Void> deleteAllByCollectionsId(Long collectionsId) {
        if (collectionsId == null) {
            return Mono.empty();
        }
        return collectionTagRepository.deleteAllByCollectionsId((collectionsId));
    }

    @Override
    public Mono<Void> saveAll(List<CollectionTag> tagRelations) {
        if (tagRelations == null || tagRelations.isEmpty()) {
            return Mono.empty();
        }
        return collectionTagRepository.saveAll(tagRelations)
                .then();
    }
}
