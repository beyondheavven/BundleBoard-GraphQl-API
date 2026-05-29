package com.source.bundleboard.client;

import com.source.bundleboard.api.exception.ClientNotFoundException;
import com.source.bundleboard.client.model.Client;
import com.source.bundleboard.client.repository.ClientRepository;
import com.source.bundleboard.client.service.ClientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientServiceImpl clientService;

    private Client sampleClient;
    private final Long userId = 42L;

    @BeforeEach
    void setUp() {
        sampleClient = new Client();
        sampleClient.setId(10L);
        sampleClient.setUserId(userId);
        sampleClient.setNewsLetterSubscription(false);
        sampleClient.setPreferredLanguage("en");
    }


    @Test
    void findByUserId_Success() {
        when(clientRepository.findByUserId(userId)).thenReturn(Mono.just(sampleClient));

        StepVerifier.create(clientService.findByUserId(userId))
                .expectNext(sampleClient)
                .verifyComplete();

        verify(clientRepository).findByUserId(userId);
    }

    @Test
    void findByUserId_NotFound_ThrowsClientNotFoundException() {
        when(clientRepository.findByUserId(userId)).thenReturn(Mono.empty());
        StepVerifier.create(clientService.findByUserId(userId))
                .expectErrorMatches(throwable -> throwable instanceof ClientNotFoundException
                        && throwable.getMessage().contains(String.valueOf(userId)))
                .verify();

        verify(clientRepository).findByUserId(userId);
    }


    @Test
    void createClientByUserId_ClientAlreadyExists_ExecutesSwitchIfEmptyDueToFlatMap() {
        when(clientRepository.findByUserId(userId)).thenReturn(Mono.just(sampleClient));
        when(clientRepository.save(any(Client.class))).thenReturn(Mono.just(sampleClient));

        StepVerifier.create(clientService.createClientByUserId(userId))
                .verifyComplete();

        verify(clientRepository).findByUserId(userId);
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    void createClientByUserId_ClientDoesNotExist_CreatesAndSavesNewClient() {
        when(clientRepository.findByUserId(userId)).thenReturn(Mono.empty());
        when(clientRepository.save(any(Client.class))).thenReturn(Mono.just(sampleClient));
        StepVerifier.create(clientService.createClientByUserId(userId))
                .verifyComplete();

        verify(clientRepository).findByUserId(userId);
        verify(clientRepository).save(argThat(client -> {
            assertEquals(userId, client.getUserId());
            assertFalse(client.getNewsLetterSubscription());
            assertEquals("en", client.getPreferredLanguage());
            return true;
        }));
    }
}
