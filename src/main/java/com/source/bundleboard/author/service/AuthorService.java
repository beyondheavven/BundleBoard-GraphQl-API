package com.source.bundleboard.author.service;


import com.source.bundleboard.author.dto.AuthorResponseDto;
import reactor.core.publisher.Mono;

public interface AuthorService {

    Mono<AuthorResponseDto> findById(Long id);

}
