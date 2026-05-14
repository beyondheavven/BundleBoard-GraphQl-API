package com.source.bundleboard.client.service;

import com.source.bundleboard.client.model.Client;
import reactor.core.publisher.Mono;

public interface ClientService {

    Mono<Client> findByUserId(Long id);
}
