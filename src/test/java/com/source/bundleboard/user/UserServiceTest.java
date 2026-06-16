package com.source.bundleboard.user;

import com.source.bundleboard.auth.jwt.JwtProperties;
import com.source.bundleboard.auth.jwt.service.JwtService;
import com.source.bundleboard.author.model.Author;
import com.source.bundleboard.author.repository.AuthorRepository;
import com.source.bundleboard.client.service.ClientService;
import com.source.bundleboard.refreshtoken.service.RefreshTokenService;
import com.source.bundleboard.user.dto.*;
import com.source.bundleboard.user.mapper.UserMapper;
import com.source.bundleboard.user.model.User;
import com.source.bundleboard.user.model.UserRole;
import com.source.bundleboard.user.model.UserStatus;
import com.source.bundleboard.user.repository.UserRepository;
import com.source.bundleboard.user.service.UserServiceImpl;
import io.r2dbc.postgresql.codec.Json;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private ClientService clientService;
    @Mock private AuthorRepository authorRepository;
    @Mock private JwtService jwtService;
    @Mock private RefreshTokenService refreshTokenService;
    @Mock private JwtProperties jwtProperties;

    @InjectMocks
    private UserServiceImpl userService;

    private User sampleUser;

    private UserResponseDto sampleResponseDto;

    private Authentication mockAuth;

    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setUsername("testuser");
        sampleUser.setEmail("test@example.com");
        sampleUser.setAvatarUrl("http://avatar.com/old.png");
        sampleUser.setStatus(UserStatus.active);
        sampleUser.setRoles(new HashSet<>(Set.of(UserRole.client)));

        sampleResponseDto = new UserResponseDto(
                1L, "testuser", "test@example.com", "http://avatar.com/old.png",
                Set.of(UserRole.client), UserStatus.active
        );

        mockAuth = new UsernamePasswordAuthenticationToken("testuser", null);
        securityContext = new SecurityContextImpl(mockAuth);
    }

    // --- BASIC READ OPERATIONS ---

    @Test
    void findUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Mono.just(sampleUser));
        when(userMapper.toDto(sampleUser)).thenReturn(sampleResponseDto);

        StepVerifier.create(userService.findUserById(1L))
                .expectNext(sampleResponseDto)
                .verifyComplete();
    }

    @Test
    void findUserByUsername_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Mono.just(sampleUser));
        when(userMapper.toDto(sampleUser)).thenReturn(sampleResponseDto);

        StepVerifier.create(userService.findUserByUsername("testuser"))
                .expectNext(sampleResponseDto)
                .verifyComplete();
    }

    @Test
    void findAllUsers_Success() {
        when(userRepository.findAll()).thenReturn(Flux.just(sampleUser));
        when(userMapper.toDto(sampleUser)).thenReturn(sampleResponseDto);

        StepVerifier.create(userService.findAllUsers())
                .expectNext(sampleResponseDto)
                .verifyComplete();
    }

    // --- CONTEXT SENSITIVE OPERATIONS ---

    @Test
    void findMe_Success() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testuser");

        SecurityContext securityContext = new SecurityContextImpl(authentication);

        when(userRepository.findByUsername("testuser")).thenReturn(Mono.just(sampleUser));
        when(userMapper.toDto(sampleUser)).thenReturn(sampleResponseDto);

        Mono<UserResponseDto> result = userService.findMe()
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));

        StepVerifier.create(result)
                .expectNext(sampleResponseDto)
                .verifyComplete();
    }

    @Test
    void updateMe_Success() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        SecurityContext securityContext = new SecurityContextImpl(authentication);
        UpdateUserRequest request = new UpdateUserRequest(1L, "new_username", "http://avatar.com/new.png");
        String accessToken = "access-token";
        String refreshToken = "refresh-token";

        UserUpdateResponse expectedResponse = new UserUpdateResponse(
                1L, "new_username", "http://avatar.com/new.png", Instant.now(), accessToken, refreshToken
        );

        when(userRepository.findById(1L)).thenReturn(Mono.just(sampleUser));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(sampleUser));

        when(jwtService.generateAccessToken(eq(1L), eq("new_username"), any())).thenReturn(accessToken);
        when(jwtService.generateRefreshToken(eq(1L), eq("new_username"))).thenReturn(refreshToken);
        when(userMapper.toUpdateResponse(any(User.class), eq(accessToken), eq(refreshToken))).thenReturn(expectedResponse);

        Mono<UserUpdateResponse> result = userService.updateMe(request)
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));

        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    // --- GET USER PROFILE ---

    @Nested
    class GetUserProfile {

        @Test
        void getClientProfile_Success() {
            Authentication auth = mock(Authentication.class);
            when(auth.getName()).thenReturn("testuser");
            when(auth.isAuthenticated()).thenReturn(true);
            SecurityContext context = new SecurityContextImpl(auth);

            when(userRepository.findByUsername("testuser")).thenReturn(Mono.just(sampleUser));

            Mono<UserProfileResponse> result = userService.getUserProfile()
                    .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));

            StepVerifier.create(result)
                    .assertNext(profile -> {
                        assertEquals(1L, profile.id());
                        assertEquals("testuser", profile.username());
                        assertNull(profile.bio());
                    })
                    .verifyComplete();
        }

        @Test
        void getAuthorProfile_Success() {
            User authorUser = new User();
            authorUser.setId(1L);
            authorUser.setUsername("testuser");
            authorUser.setEmail("test@example.com");
            authorUser.setRoles(new HashSet<>(Set.of(UserRole.author)));

            Authentication auth = mock(Authentication.class);
            when(auth.getName()).thenReturn("testuser");
            when(auth.isAuthenticated()).thenReturn(true);
            SecurityContext context = new SecurityContextImpl(auth);

            Author sampleAuthor = new Author();
            sampleAuthor.setBio("My awesome bio");
            sampleAuthor.setSocialLinks(Json.of("{}"));

            when(userRepository.findByUsername("testuser")).thenReturn(Mono.just(authorUser));
            when(authorRepository.findByUserId(1L)).thenReturn(Mono.just(sampleAuthor));

            Mono<UserProfileResponse> result = userService.getUserProfile()
                    .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));

            StepVerifier.create(result)
                    .assertNext(profile -> {
                        assertEquals("My awesome bio", profile.bio());
                        assertTrue(profile.roles().contains(UserRole.author));
                    })
                    .verifyComplete();
        }
    }

    // --- UTILITY UPDATES ---

    @Test
    void updateUserAvatar_Success() {
        UpdateAvatarRequest request = new UpdateAvatarRequest(1L, "http://avatar.com/new.png");

        when(userRepository.findById(1L)).thenReturn(Mono.just(sampleUser));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(sampleUser));

        StepVerifier.create(userService.updateUserAvatar(request))
                .assertNext(response -> assertEquals("http://avatar.com/new.png", response.avatarUrl()))
                .verifyComplete();
    }

    @Test
    void getUserCommentResponseById_Success() {
        when(userRepository.findById(1L)).thenReturn(Mono.just(sampleUser));

        StepVerifier.create(userService.getUserCommentResponseById(1L))
                .assertNext(response -> {
                    assertEquals(1L, response.id());
                    assertEquals("testuser", response.username());
                    assertEquals("http://avatar.com/old.png", response.avatarUrl());
                })
                .verifyComplete();
    }
}