package com.source.bundleboard.author.service;


import com.source.bundleboard.author.model.Author;
import reactor.core.publisher.Mono;

public interface AuthorService {

    Mono<Author> existsById(Long id);

}
