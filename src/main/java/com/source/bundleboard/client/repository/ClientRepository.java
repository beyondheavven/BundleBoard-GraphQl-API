package com.source.bundleboard.client.repository;

import com.source.bundleboard.client.model.Client;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ClientRepository extends R2dbcRepository<Client, Long> {
    Mono<Client> findByUserId(Long userId);
}
