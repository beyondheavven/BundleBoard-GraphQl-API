package com.source.bundleboard.author;

import com.source.bundleboard.api.exception.AuthorNotFoundException;
import com.source.bundleboard.author.dto.*;
import com.source.bundleboard.author.mapper.AuthorMapper;
import com.source.bundleboard.author.model.Author;
import com.source.bundleboard.author.repository.AuthorRepository;
import com.source.bundleboard.author.service.AuthorServiceImpl;
import com.source.bundleboard.user.dto.UserProfileResponse;
import com.source.bundleboard.user.model.User;
import com.source.bundleboard.user.model.UserRole;
import com.source.bundleboard.user.model.UserStatus;
import com.source.bundleboard.user.service.UserService;
import io.r2dbc.postgresql.codec.Json;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;

@ExtendWith(MockitoExtension.class)
public class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private UserService userService;

    @Mock
    private AuthorMapper authorMapper;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AuthorServiceImpl authorService;

    private Author sampleAuthor;
    private User sampleUser;
    private BaseAuthorResponse sampleBaseAuthorResponse;
    private UserProfileResponse sampleUserProfileResponse;

    private final Long authorId = 1L;
    private final Long userId = 42L;

    @BeforeEach
    void setUp() {
        sampleAuthor = new Author();
        sampleAuthor.setId(authorId);
        sampleAuthor.setUserId(userId);
        sampleAuthor.setBio("Experienced content creator");
        sampleAuthor.setRating(new BigDecimal("4.85"));
        sampleAuthor.setTotalSales(150);
        sampleAuthor.setSocialLinks(Json.of("[{\"platform\":\"github\",\"url\":\"https://github.com\"}]"));
        sampleAuthor.setStripeAccountId("acct_123456");

        sampleUser = new User();
        sampleUser.setId(userId);
        sampleUser.setUsername("author_jack");
        sampleUser.setEmail("jack@example.com");
        sampleUser.setAvatarUrl("http://avatar.url/jack.png");
        sampleUser.setStatus(UserStatus.active);
        sampleUser.setRoles(Set.of(UserRole.client));

        sampleBaseAuthorResponse = new BaseAuthorResponse(
                authorId,
                "Experienced content creator",
                new BigDecimal("4.85"),
                150,
                "[{\"platform\":\"github\",\"url\":\"https://github.com\"}]",
                "acct_123456"
        );

        sampleUserProfileResponse = new UserProfileResponse(
                userId,
                "author_jack",
                "jack@example.com",
                "http://avatar.url/jack.png",
                UserStatus.active,
                Set.of(UserRole.client),
                List.of(),
                List.of(),
                "Experienced content creator",
                List.of(new SocialLink("github", "https://github.com")),
                "acct_123456"
        );
    }

    // --- getAuthorBaseResponseById ---

    @Test
    void getAuthorBaseResponseById_Success() {
        when(authorRepository.findByUserId(userId)).thenReturn(Mono.just(sampleAuthor));
        when(authorMapper.toDto(sampleAuthor)).thenReturn(sampleBaseAuthorResponse);

        StepVerifier.create(authorService.getAuthorBaseResponseById(userId))
                .assertNext(response -> {
                    assertEquals(authorId, response.id());
                    assertEquals("Experienced content creator", response.bio());
                    assertEquals(new BigDecimal("4.85"), response.rating());
                    assertEquals(150, response.totalSales());
                    assertEquals("acct_123456", response.stripeAccountId());
                    assertNotNull(response.socialLinks());
                })
                .verifyComplete();

        verify(authorRepository).findByUserId(userId);
        verify(authorMapper).toDto(sampleAuthor);
    }

    @Test
    void getAuthorBaseResponseById_NotFound_ThrowsAuthorNotFoundException() {
        when(authorRepository.findByUserId(userId)).thenReturn(Mono.empty());

        StepVerifier.create(authorService.getAuthorBaseResponseById(userId))
                .expectError(AuthorNotFoundException.class)
                .verify();

        verifyNoInteractions(authorMapper);
    }

    // --- findShortResponseById ---

    @Test
    void findShortResponseById_Success() {
        when(authorRepository.findById(authorId)).thenReturn(Mono.just(sampleAuthor));
        when(userService.getUserById(userId)).thenReturn(Mono.just(sampleUser));

        StepVerifier.create(authorService.findShortResponseById(authorId))
                .assertNext(response -> {
                    assertEquals(authorId, response.id());
                    assertEquals(new BigDecimal("4.85"), response.rating());
                    assertEquals(150, response.totalSales());
                    assertEquals("author_jack", response.username());
                })
                .verifyComplete();
    }

    @Test
    void findShortResponseById_AuthorNotFound_ThrowsException() {
        when(authorRepository.findById(authorId)).thenReturn(Mono.empty());

        StepVerifier.create(authorService.findShortResponseById(authorId))
                .expectError(AuthorNotFoundException.class)
                .verify();

        verifyNoInteractions(userService);
    }

    // --- findFullAuthorById ---

    @Test
    void findFullAuthorById_Success() {
        when(authorRepository.findById(authorId)).thenReturn(Mono.just(sampleAuthor));
        when(userService.getUserById(userId)).thenReturn(Mono.just(sampleUser));

        StepVerifier.create(authorService.findFullAuthorById(authorId))
                .assertNext(response -> {
                    assertEquals(authorId, response.id());
                    assertEquals("Experienced content creator", response.bio());
                    assertEquals("author_jack", response.username());
                    assertEquals("jack@example.com", response.email());
                    assertEquals(1, response.socialLinks().size());
                    assertEquals("github", response.socialLinks().get(0).platform());
                })
                .verifyComplete();
    }

    @Test
    void findFullAuthorById_Success_WhenSocialLinksNull() {
        sampleAuthor.setSocialLinks(null);
        when(authorRepository.findById(authorId)).thenReturn(Mono.just(sampleAuthor));
        when(userService.getUserById(userId)).thenReturn(Mono.just(sampleUser));

        StepVerifier.create(authorService.findFullAuthorById(authorId))
                .assertNext(response -> assertTrue(response.socialLinks().isEmpty()))
                .verifyComplete();
    }

    // --- findByUsername ---

    @Test
    void findByUsername_Success() {
        when(userService.findByUsername("author_jack")).thenReturn(Mono.just(sampleUser));
        when(authorRepository.findByUserId(userId)).thenReturn(Mono.just(sampleAuthor));

        StepVerifier.create(authorService.findByUsername("author_jack"))
                .expectNext(sampleAuthor)
                .verifyComplete();
    }

    @Test
    void findByUsername_NotFound() {
        when(userService.findByUsername("unknown")).thenReturn(Mono.error(new AuthorNotFoundException()));

        StepVerifier.create(authorService.findByUsername("unknown"))
                .expectError(AuthorNotFoundException.class)
                .verify();
    }

    // --- findById & findByUserId ---

    @Test
    void findById_Success() {
        when(authorRepository.findById(authorId)).thenReturn(Mono.just(sampleAuthor));
        StepVerifier.create(authorService.findById(authorId))
                .expectNext(sampleAuthor)
                .verifyComplete();
    }

    @Test
    void findById_Null() {
        StepVerifier.create(authorService.findById(null))
                .verifyComplete();
    }

    @Test
    void findByUserId_Success() {
        when(authorRepository.findByUserId(userId)).thenReturn(Mono.just(sampleAuthor));
        StepVerifier.create(authorService.findByUserId(userId))
                .expectNext(sampleAuthor)
                .verifyComplete();
    }

    // --- updateProfileDetails ---

    @Test
    void updateProfileDetails_Success() throws Exception {
        List<SocialLinkInput> newLinks = List.of(new SocialLinkInput("twitter", "https://twitter.com"));
        String jsonLinks = "[{\"platform\":\"twitter\",\"url\":\"https://twitter.com\"}]";

        when(userService.findByUsername("author_jack")).thenReturn(Mono.just(sampleUser));
        when(authorRepository.findByUserId(userId)).thenReturn(Mono.just(sampleAuthor));
        when(objectMapper.writeValueAsString(newLinks)).thenReturn(jsonLinks);
        when(authorRepository.save(any(Author.class))).thenReturn(Mono.just(sampleAuthor));
        when(userService.getUserProfile()).thenReturn(Mono.just(sampleUserProfileResponse));

        Authentication authentication = new UsernamePasswordAuthenticationToken("author_jack", "password");
        SecurityContext securityContext = new SecurityContextImpl(authentication);

        StepVerifier.create(authorService.updateProfileDetails("New Bio", newLinks)
                        .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext))))
                .expectNext(sampleUserProfileResponse)
                .verifyComplete();

        verify(authorRepository).save(argThat(author -> author.getBio().equals("New Bio") && author.getSocialLinks().asString().equals(jsonLinks)));
    }

    @Test
    void updateProfileDetails_WithNullSocialLinks_Success() {
        when(userService.findByUsername("author_jack")).thenReturn(Mono.just(sampleUser));
        when(authorRepository.findByUserId(userId)).thenReturn(Mono.just(sampleAuthor));
        when(authorRepository.save(any(Author.class))).thenReturn(Mono.just(sampleAuthor));
        when(userService.getUserProfile()).thenReturn(Mono.just(sampleUserProfileResponse));

        Authentication authentication = new UsernamePasswordAuthenticationToken("author_jack", "password");
        SecurityContext securityContext = new SecurityContextImpl(authentication);

        StepVerifier.create(authorService.updateProfileDetails("New Bio", null)
                        .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext))))
                .expectNext(sampleUserProfileResponse)
                .verifyComplete();

        verify(authorRepository).save(argThat(author -> author.getBio().equals("New Bio") && author.getSocialLinks() == null));
    }

    @Test
    void updateProfileDetails_JsonSerializationFails_ThrowsException() throws Exception {
        List<SocialLinkInput> newLinks = List.of(new SocialLinkInput("twitter", "https://twitter.com"));

        when(userService.findByUsername("author_jack")).thenReturn(Mono.just(sampleUser));
        when(authorRepository.findByUserId(userId)).thenReturn(Mono.just(sampleAuthor));
        when(objectMapper.writeValueAsString(newLinks)).thenThrow(new RuntimeException("JSON Error"));

        Authentication authentication = new UsernamePasswordAuthenticationToken("author_jack", "password");
        SecurityContext securityContext = new SecurityContextImpl(authentication);

        StepVerifier.create(authorService.updateProfileDetails("New Bio", newLinks)
                        .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext))))
                .expectError(RuntimeException.class)
                .verify();

        verify(authorRepository, never()).save(any());
    }

    // --- incrementSalesAndRating ---

    @Test
    void incrementSalesAndRating_Success() {
        when(authorRepository.incrementSalesAndRating(authorId)).thenReturn(Mono.empty());

        StepVerifier.create(authorService.incrementSalesAndRating(authorId))
                .verifyComplete();

        verify(authorRepository).incrementSalesAndRating(authorId);
    }

    @Test
    void incrementSalesAndRating_NullId_ThrowsException() {
        StepVerifier.create(authorService.incrementSalesAndRating(null))
                .expectError(RuntimeException.class)
                .verify();

        verify(authorRepository, never()).incrementSalesAndRating(anyLong());
    }
}