package com.source.bundleboard.collection.repository;

import com.source.bundleboard.collection.model.Collection;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionRepository extends R2dbcRepository<Collection, Long> {
}
