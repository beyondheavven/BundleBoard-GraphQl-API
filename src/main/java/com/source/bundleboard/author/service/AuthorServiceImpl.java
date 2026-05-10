package com.source.bundleboard.author.service;

import com.source.bundleboard.api.exception.AuthorNotFoundException;
import com.source.bundleboard.author.dto.AuthorResponse;
import com.source.bundleboard.author.dto.AuthorShortResponse;
import com.source.bundleboard.author.dto.BaseAuthorResponse;
import com.source.bundleboard.author.mapper.AuthorMapper;
import com.source.bundleboard.author.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    private final AuthorMapper authorMapper;

    @Override
    public Mono<BaseAuthorResponse> findById(Long id) {
        return authorRepository.findByUserId(id)
                .map(authorMapper::toDto)
                .switchIfEmpty(Mono.error(new AuthorNotFoundException()));
    }

    @Override
    public Mono<AuthorShortResponse> findShortResponseById(Long id) {
        return authorRepository.findAuthorWithUsernameById(id)
                .map(authorMapper::toShortDto)
                .switchIfEmpty(Mono.error(new AuthorNotFoundException()));
    }


}
