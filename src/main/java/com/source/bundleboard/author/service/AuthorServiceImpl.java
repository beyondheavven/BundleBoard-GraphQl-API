package com.source.bundleboard.author.service;

import com.source.bundleboard.api.exception.AuthorNotFoundException;
import com.source.bundleboard.author.model.Author;
import com.source.bundleboard.author.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    @Override
    public Mono<Author> existsById(Long id) {
        return authorRepository.findById(id).switchIfEmpty(Mono.error(new AuthorNotFoundException("Author not found.")));
    }

}
