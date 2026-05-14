package com.source.bundleboard.client.service;

import com.source.bundleboard.api.exception.ClientNotFoundException;
import com.source.bundleboard.client.model.Client;
import com.source.bundleboard.client.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Override
    public Mono<Client> findByUserId(Long id) {
        return clientRepository.findByUserId(id)
                .switchIfEmpty(Mono.error(() -> (new ClientNotFoundException(id))));
    }
}
