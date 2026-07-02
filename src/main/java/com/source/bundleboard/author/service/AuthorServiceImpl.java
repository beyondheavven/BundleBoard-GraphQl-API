package com.source.bundleboard.author.service;

import com.source.bundleboard.api.exception.AuthorNotFoundException;
import com.source.bundleboard.author.dto.*;
import com.source.bundleboard.author.mapper.AuthorMapper;
import com.source.bundleboard.author.model.Author;
import com.source.bundleboard.author.repository.AuthorRepository;
import com.source.bundleboard.user.dto.UserProfileResponse;
import com.source.bundleboard.user.service.UserService;
import io.r2dbc.postgresql.codec.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Objects;

import static com.source.bundleboard.utils.SocialLinksParser.parseSocialLinks;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    private final AuthorMapper authorMapper;

    private final ObjectMapper objectMapper;

    private final UserService userService;

    @Override
    public Mono<BaseAuthorResponse> getAuthorBaseResponseById(Long id) {
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
                                author.getUserId(),
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

    @Override
    public Mono<Author> findByUsername(String username) {
        return userService.findByUsername(username)
                .flatMap(user -> authorRepository.findByUserId(user.getId()))
                .switchIfEmpty(Mono.error(new AuthorNotFoundException()));
    }

    @Override
    public Mono<Author> findById(Long id) {
        if (id == null) {
            return Mono.empty();
        }
        return authorRepository.findById(id);
    }

    @Override
    public Mono<Author> findByUserId(Long userId) {
        return authorRepository.findByUserId(userId)
                .switchIfEmpty(Mono.error(new AuthorNotFoundException()));
    }

    @Override
    public Mono<UserProfileResponse> updateProfileDetails(String bio, List<SocialLinkInput> socialLinks) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> Objects.requireNonNull(ctx.getAuthentication()).getName())
                .flatMap(this::findByUsername)
                .flatMap(author -> {
                    author.setBio(bio);
                    try {
                        if(socialLinks != null) {
                            String jsonLinks = objectMapper.writeValueAsString(socialLinks);
                            author.setSocialLinks(Json.of(jsonLinks));
                        }else {
                            author.setSocialLinks(null);
                        }
                    }catch (Exception e){
                        log.error("🔴 [JSON_SERIALIZE_ERROR]: Failed to encode social links.", e);
                        return Mono.error(new RuntimeException("Failed to serialize social links", e));
                    }
                    return authorRepository.save(author);
                })
                .flatMap(savedAuthor -> userService.getUserProfile());
    }

    @Override
    public Mono<Void> incrementSalesAndRating(Long authorId) {
        if (authorId == null) {
            log.error("Author ID is null.");
            return Mono.error(new RuntimeException("Author ID is null."));
        }
        return authorRepository.incrementSalesAndRating(authorId).then();
    }

}
