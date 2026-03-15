package com.source.bundleboard.author.service;

import com.source.bundleboard.api.exception.AuthorNotFoundException;
import com.source.bundleboard.author.dto.AuthorResponseDto;
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
    public Mono<AuthorResponseDto> findById(Long id) {
        return authorRepository.findByUserId(id)
                .map(authorMapper::toDto)
                .switchIfEmpty(Mono.error(new AuthorNotFoundException("Author not found.")));
    }


}
