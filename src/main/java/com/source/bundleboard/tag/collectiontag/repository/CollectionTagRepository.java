package com.source.bundleboard.tag.collectiontag.repository;

import com.source.bundleboard.tag.collectiontag.model.CollectionTag;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionTagRepository extends R2dbcRepository<CollectionTag, Long> {
}
