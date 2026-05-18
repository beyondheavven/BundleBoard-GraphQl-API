package com.source.bundleboard.author.service;

import com.source.bundleboard.api.exception.AuthorNotFoundException;
import com.source.bundleboard.author.dto.AuthorResponse;
import com.source.bundleboard.author.dto.AuthorShortResponse;
import com.source.bundleboard.author.dto.BaseAuthorResponse;
import com.source.bundleboard.author.dto.SocialLink;
import com.source.bundleboard.author.mapper.AuthorMapper;
import com.source.bundleboard.author.model.Author;
import com.source.bundleboard.author.repository.AuthorRepository;
import com.source.bundleboard.user.service.UserService;
import io.r2dbc.postgresql.codec.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    private final UserService userService;

    private final AuthorMapper authorMapper;

    private final ObjectMapper objectMapper;

    @Override
    public Mono<BaseAuthorResponse> findById(Long id) {
        return authorRepository.findByUserId(id)
                .map(authorMapper::toDto)
                .switchIfEmpty(Mono.error(new AuthorNotFoundException()));
    }

    @Override
    public Mono<AuthorShortResponse> findShortResponseById(Long id) {
        return authorRepository.findById(id)
                .flatMap(author -> userService.getUserById(author.getUserId())
                        .map(user -> new AuthorShortResponse(
                                author.getId(),
                                author.getRating(),
                                author.getTotalSales(),
                                user.getUsername()
                        ))
                )
                .switchIfEmpty(Mono.error(new AuthorNotFoundException()));
    }

    @Override
    public Mono<AuthorResponse> findFullAuthorById(Long id) {
        return authorRepository.findById(id)
                .flatMap(author -> userService.getUserById(author.getUserId())
                        .map(user -> new AuthorResponse(
                                author.getId(),
                                author.getBio(),
                                author.getRating(),
                                author.getTotalSales(),
                                parseSocialLinks(author.getSocialLinks()),
                                user.getUsername(),
                                user.getEmail(),
                                user.getAvatarUrl()
                        )))
                .switchIfEmpty(Mono.error(new AuthorNotFoundException()));
    }

    private List<SocialLink> parseSocialLinks(Json json) {
        if (json == null) return List.of();

        try {
            return objectMapper.readValue(
                    json.asString(),
                    new TypeReference<List<SocialLink>>() {}
            );
        } catch (Exception e) {
            log.error("[JSON_PARSE_ERROR]: Failed to decode social links. Data: {}", json.asString(), e);
            return List.of();
        }
    }


}
