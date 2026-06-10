package com.source.bundleboard.user;

import com.source.bundleboard.auth.jwt.JwtProperties;
import com.source.bundleboard.auth.jwt.service.JwtService;
import com.source.bundleboard.author.repository.AuthorRepository;
import com.source.bundleboard.client.model.Client;
import com.source.bundleboard.client.service.ClientService;
import com.source.bundleboard.purchase.service.PurchaseService;
import com.source.bundleboard.refreshtoken.service.RefreshTokenService;
import com.source.bundleboard.user.dto.*;
import com.source.bundleboard.user.mapper.UserMapper;
import com.source.bundleboard.user.model.User;
import com.source.bundleboard.user.model.UserRole;
import com.source.bundleboard.user.model.UserStatus;
import com.source.bundleboard.user.repository.UserRepository;
import com.source.bundleboard.user.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private PurchaseService purchaseService;
    @Mock private ClientService clientService;
    @Mock private AuthorRepository authorRepository;
    @Mock private JwtService jwtService;
    @Mock private RefreshTokenService refreshTokenService;
    @Mock private JwtProperties jwtProperties;

    @InjectMocks
    private UserServiceImpl userService;

    private User sampleUser;
    private UserResponseDto sampleResponseDto;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setUsername("testuser");
        sampleUser.setEmail("test@example.com");
        sampleUser.setAvatarUrl("http://avatar.com/old.png");
        sampleUser.setStatus(UserStatus.active);
        sampleUser.setRoles(Set.of(UserRole.client));

        sampleResponseDto = new UserResponseDto(
                1L,
                "testuser",
                "test@example.com",
                "http://avatar.com/old.png",
                Set.of(UserRole.client),
                UserStatus.active
        );
    }


    @Test
    void findUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Mono.just(sampleUser));
        when(userMapper.toDto(sampleUser)).thenReturn(sampleResponseDto);

        StepVerifier.create(userService.findUserById(1L))
                .expectNext(sampleResponseDto)
                .verifyComplete();
    }

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
    void getUserProfile_Success() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContext securityContext = new SecurityContextImpl(authentication);

        Client mockClient = new Client();
        mockClient.setId(10L);

        when(userRepository.findByUsername("testuser")).thenReturn(Mono.just(sampleUser));
        when(clientService.findByUserId(1L)).thenReturn(Mono.just(mockClient));
        when(purchaseService.findAllByUserId(10L)).thenReturn(Mono.just(Collections.emptyList()));

        Mono<UserProfileResponse> result = userService.getUserProfile()
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));

        StepVerifier.create(result)
                .assertNext(profile -> {
                    org.junit.jupiter.api.Assertions.assertEquals(1L, profile.id());
                    org.junit.jupiter.api.Assertions.assertEquals("testuser", profile.username());
                    org.junit.jupiter.api.Assertions.assertEquals(UserStatus.active, profile.status());
                    org.junit.jupiter.api.Assertions.assertTrue(profile.purchases().isEmpty());
                })
                .verifyComplete();
    }


    @Test
    void updateMe_Success() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContext securityContext = new SecurityContextImpl(authentication);

        UpdateUserRequest request = new UpdateUserRequest("new_username", "http://avatar.com/new.png");
        String dummyAccessToken = "mock-secure-access-token-string";
        String dummyRefreshToken = "mock-secure-refresh-token-string";
        UserUpdateResponse expectedResponse = new UserUpdateResponse(
                1L,
                "new_username",
                "http://avatar.com/new.png",
                Instant.now(),
                dummyAccessToken,
                dummyRefreshToken
        );

        when(userRepository.findByUsername("testuser")).thenReturn(Mono.just(sampleUser));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(sampleUser));

        when(jwtService.generateAccessToken(eq("new_username"), anyCollection()))
                .thenReturn(dummyAccessToken);
        when(jwtService.generateRefreshToken(eq("new_username")))
                .thenReturn(dummyRefreshToken);

        when(userMapper.toUpdateResponse(any(User.class), eq(dummyAccessToken), eq(dummyRefreshToken)))
                .thenReturn(expectedResponse);

        Mono<UserUpdateResponse> result = userService.updateMe(request)
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));

        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(userRepository).save(argThat(user ->
                "new_username".equals(user.getUsername()) &&
                        "http://avatar.com/new.png".equals(user.getAvatarUrl())
        ));

        verify(jwtService).generateAccessToken(eq("new_username"), anyCollection());
        verify(jwtService).generateRefreshToken(eq("new_username"));
    }

    @Test
    void updateAvatar_Success() {
        UpdateAvatarRequest request = new UpdateAvatarRequest(1L, "http://avatar.com/avatar3.png");

        when(userRepository.findById(1L)).thenReturn(Mono.just(sampleUser));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(sampleUser));

        StepVerifier.create(userService.updateUserAvatar(request))
                .assertNext(response -> {
                    org.junit.jupiter.api.Assertions.assertEquals(1L, response.id());
                    org.junit.jupiter.api.Assertions.assertEquals("http://avatar.com/avatar3.png", response.avatarUrl());
                    org.junit.jupiter.api.Assertions.assertNotNull(response.updatedAt());
                })
                .verifyComplete();
    }

    @Test
    void updateUserRole_Success() {
        UpdateUserRoleInput input = new UpdateUserRoleInput("test@example.com", "author");

        sampleUser.setRoles(new HashSet<>(sampleUser.getRoles()));
        sampleUser.getRoles().clear();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Mono.just(sampleUser));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(sampleUser));

        when(authorRepository.findByUserId(1L)).thenReturn(Mono.empty());
        when(authorRepository.save(any())).thenReturn(Mono.empty());

        when(jwtService.generateAccessToken(any(), any())).thenReturn("access-token-123");
        when(jwtService.generateRefreshToken(any())).thenReturn("refresh-token-123");
        when(jwtProperties.getRefreshTokenExpirationMs()).thenReturn(60000L);
        when(refreshTokenService.deleteByUserId(1L)).thenReturn(Mono.empty());

        com.source.bundleboard.refreshtoken.model.RefreshToken mockToken =
                new com.source.bundleboard.refreshtoken.model.RefreshToken(null, 1L, "refresh-token-123", Instant.now(), Instant.now());
        when(refreshTokenService.save(any())).thenReturn(Mono.just(mockToken));

        StepVerifier.create(userService.updateUserRole(input))
                .assertNext(response -> {
                    org.junit.jupiter.api.Assertions.assertTrue(response.success());
                    org.junit.jupiter.api.Assertions.assertEquals("access-token-123", response.accessToken());
                    org.junit.jupiter.api.Assertions.assertEquals("refresh-token-123", response.refreshToken());
                })
                .verifyComplete();
    }
}