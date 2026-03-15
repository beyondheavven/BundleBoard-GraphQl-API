package com.source.bundleboard.mediaresource.repository;

import com.source.bundleboard.mediaresource.model.MediaResource;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaResourceRepository extends R2dbcRepository<MediaResource, Long> {
}
