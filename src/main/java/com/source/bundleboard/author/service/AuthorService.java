package com.source.bundleboard.author.service;


import com.source.bundleboard.author.dto.AuthorResponse;
import com.source.bundleboard.author.dto.AuthorShortResponse;
import com.source.bundleboard.author.dto.BaseAuthorResponse;
import com.source.bundleboard.author.dto.SocialLinkInput;
import com.source.bundleboard.author.model.Author;
import com.source.bundleboard.user.dto.UserProfileResponse;
import com.source.bundleboard.user.model.User;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AuthorService {

    Mono<BaseAuthorResponse> getAuthorBaseResponseById(Long id);

    Mono<AuthorShortResponse> findShortResponseById(Long id);

    Mono<AuthorResponse> findFullAuthorById(Long id);

    Mono<Author> findByUsername(String username);

    Mono<Author> findById(Long id);

    Mono<Author> findByUserId(Long userId);

    Mono<User> findUserByAuthorId(Long authorId);

    Mono<UserProfileResponse> updateProfileDetails(String bio, List<SocialLinkInput> socialLinks);

    Mono<Void> incrementSalesAndRating(Long authorId);
}
