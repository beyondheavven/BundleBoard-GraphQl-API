package com.source.bundleboard.author;

import com.source.bundleboard.api.exception.AuthorNotFoundException;
import com.source.bundleboard.author.dto.BaseAuthorResponse;
import com.source.bundleboard.author.dto.SocialLink;
import com.source.bundleboard.author.mapper.AuthorMapper;
import com.source.bundleboard.author.model.Author;
import com.source.bundleboard.author.repository.AuthorRepository;
import com.source.bundleboard.author.service.AuthorServiceImpl;
import com.source.bundleboard.user.model.User;
import com.source.bundleboard.user.repository.UserRepository;
import io.r2dbc.postgresql.codec.Json;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthorMapper authorMapper;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AuthorServiceImpl authorService;

    private Author sampleAuthor;

    private User sampleUser;

    private BaseAuthorResponse sampleBaseAuthorResponse;

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

        sampleUser = new User();
        sampleUser.setId(userId);
        sampleUser.setUsername("author_jack");
        sampleUser.setEmail("jack@example.com");
        sampleUser.setAvatarUrl("http://avatar.url/jack.png");

        sampleBaseAuthorResponse = new BaseAuthorResponse(
                authorId,
                "Experienced content creator",
                new BigDecimal("4.85"),
                150,
                "[{\"platform\":\"github\",\"url\":\"https://github.com\"}]",
                "acct_123456"
        );
    }


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


    @Test
    void findShortResponseById_Success() {
        when(authorRepository.findById(authorId)).thenReturn(Mono.just(sampleAuthor));
        when(userRepository.findById(userId)).thenReturn(Mono.just(sampleUser));

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

        verifyNoInteractions(userRepository);
    }

    @Test
    void findShortResponseById_UserNotFound_ThrowsException() {
        when(authorRepository.findById(authorId)).thenReturn(Mono.just(sampleAuthor));
        when(userRepository.findById(userId)).thenReturn(Mono.empty());

        StepVerifier.create(authorService.findShortResponseById(authorId))
                .expectError(AuthorNotFoundException.class)
                .verify();
    }


    @Test
    @SuppressWarnings("unchecked")
    void findFullAuthorById_Success_WithParsedSocialLinks() throws Exception {
        List<SocialLink> expectedLinks = List.of(new SocialLink("github", "https://github.com"));

        when(authorRepository.findById(authorId)).thenReturn(Mono.just(sampleAuthor));
        when(userRepository.findById(userId)).thenReturn(Mono.just(sampleUser));

        doReturn(expectedLinks)
                .when(objectMapper).readValue(
                        eq(sampleAuthor.getSocialLinks().asString()),
                        any(TypeReference.class)
                );

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
    @SuppressWarnings("unchecked")
    void findFullAuthorById_Success_WhenSocialLinksJsonIsInvalid() throws Exception {
        when(authorRepository.findById(authorId)).thenReturn(Mono.just(sampleAuthor));
        when(userRepository.findById(userId)).thenReturn(Mono.just(sampleUser));

        doThrow(new RuntimeException("Jackson error"))
                .when(objectMapper).readValue(
                        any(String.class),
                        any(TypeReference.class)
                );

        StepVerifier.create(authorService.findFullAuthorById(authorId))
                .assertNext(response -> {
                    assertEquals(authorId, response.id());
                    assertTrue(response.socialLinks().isEmpty());
                })
                .verifyComplete();
    }

    @Test
    void findFullAuthorById_Success_WhenSocialLinksNull() {
        sampleAuthor.setSocialLinks(null);

        when(authorRepository.findById(authorId)).thenReturn(Mono.just(sampleAuthor));
        when(userRepository.findById(userId)).thenReturn(Mono.just(sampleUser));

        StepVerifier.create(authorService.findFullAuthorById(authorId))
                .assertNext(response -> {
                    assertTrue(response.socialLinks().isEmpty());
                })
                .verifyComplete();

        verifyNoInteractions(objectMapper);
    }


}
