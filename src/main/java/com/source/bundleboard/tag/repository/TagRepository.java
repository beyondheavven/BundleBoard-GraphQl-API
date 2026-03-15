package com.source.bundleboard.tag.repository;

import com.source.bundleboard.tag.model.Tag;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends R2dbcRepository<Tag, Long> {
}
