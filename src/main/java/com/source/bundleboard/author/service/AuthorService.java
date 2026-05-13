package com.source.bundleboard.author.service;


import com.source.bundleboard.author.dto.AuthorResponse;
import com.source.bundleboard.author.dto.AuthorShortResponse;
import com.source.bundleboard.author.dto.BaseAuthorResponse;
import reactor.core.publisher.Mono;

public interface AuthorService {

    Mono<BaseAuthorResponse> findById(Long id);

    Mono<AuthorShortResponse> findShortResponseById(Long id);

    Mono<AuthorResponse> findFullAuthorById(Long id);
}
