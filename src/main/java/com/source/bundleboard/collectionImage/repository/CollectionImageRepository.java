package com.source.bundleboard.collectionImage.repository;

import com.source.bundleboard.collectionImage.model.CollectionImage;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionImageRepository extends R2dbcRepository<CollectionImage, Long> {
}
