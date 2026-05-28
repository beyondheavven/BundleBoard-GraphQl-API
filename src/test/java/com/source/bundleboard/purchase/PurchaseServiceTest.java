package com.source.bundleboard.purchase;

import com.source.bundleboard.collection.dto.CollectionShortResponse;
import com.source.bundleboard.collection.service.CollectionService;
import com.source.bundleboard.purchase.dto.PurchaseBaseResponse;
import com.source.bundleboard.purchase.mapper.PurchaseMapper;
import com.source.bundleboard.purchase.model.Purchase;
import com.source.bundleboard.purchase.model.PurchaseStatus;
import com.source.bundleboard.purchase.repository.PurchaseRepository;
import com.source.bundleboard.purchase.service.PurchaseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PurchaseServiceTest {

    @Mock
    private CollectionService collectionService;

    @Mock
    private PurchaseMapper purchaseMapper;

    @Mock
    private PurchaseRepository purchaseRepository;

    @InjectMocks
    private PurchaseServiceImpl purchaseService;

    private Purchase purchase1;
    private Purchase purchase2;
    private CollectionShortResponse mockAsset;
    private PurchaseBaseResponse response1;
    private PurchaseBaseResponse response2;

    @BeforeEach
    void setUp() {

        purchase1 = new Purchase();
        purchase1.setId(1L);
        purchase1.setClientId(100L);
        purchase1.setCollectionId(501L);

        purchase2 = new Purchase();
        purchase2.setId(2L);
        purchase2.setClientId(100L);
        purchase2.setCollectionId(502L);

        mockAsset = new CollectionShortResponse(501L, "Test Collection", null);

        Instant now = Instant.now();

        response1 = new PurchaseBaseResponse(
                1L,
                99.99,
                "USD",
                PurchaseStatus.succeeded,
                99.99,
                now,
                mockAsset
        );

        response2 = new PurchaseBaseResponse(
                2L,
                49.99,
                "USD",
                PurchaseStatus.succeeded,
                49.99,
                now,
                mockAsset
        );
    }

    @Test
    void findAllByClientId_Success_ReturnsEnrichedList() {
        Long clientId = 100L;

        when(purchaseRepository.findAllByClientId(clientId))
                .thenReturn(Flux.just(purchase1, purchase2));

        when(collectionService.findShortResponseById(501L)).thenReturn(Mono.just(mockAsset));
        when(collectionService.findShortResponseById(502L)).thenReturn(Mono.just(mockAsset));

        when(purchaseMapper.toBaseResponse(eq(purchase1), any(CollectionShortResponse.class))).thenReturn(response1);
        when(purchaseMapper.toBaseResponse(eq(purchase2), any(CollectionShortResponse.class))).thenReturn(response2);

        Mono<List<PurchaseBaseResponse>> result = purchaseService.findAllByClientId(clientId);

        StepVerifier.create(result)
                .assertNext(list -> {
                    assertNotNull(list);
                    assertEquals(2, list.size());

                    PurchaseBaseResponse first = list.get(0);
                    assertEquals(1L, first.id());
                    assertEquals("USD", first.currency());
                    assertEquals(PurchaseStatus.succeeded, first.status());
                    assertEquals(mockAsset, first.asset());

                    assertEquals(response2, list.get(1));
                })
                .verifyComplete();

        // Проверка вызовов зависимостей
        verify(purchaseRepository, times(1)).findAllByClientId(clientId);
        verify(collectionService, times(1)).findShortResponseById(501L);
        verify(collectionService, times(1)).findShortResponseById(502L);
        verify(purchaseMapper, times(2)).toBaseResponse(any(), any());
    }

    @Test
    void findAllByClientId_NoPurchases_ReturnsEmptyList() {
        Long clientId = 100L;

        when(purchaseRepository.findAllByClientId(clientId)).thenReturn(Flux.empty());

        Mono<List<PurchaseBaseResponse>> result = purchaseService.findAllByClientId(clientId);

        StepVerifier.create(result)
                .assertNext(list -> {
                    assertNotNull(list);
                    assertTrue(list.isEmpty());
                })
                .verifyComplete();

        verify(purchaseRepository, times(1)).findAllByClientId(clientId);
        verifyNoInteractions(collectionService);
        verifyNoInteractions(purchaseMapper);
    }
}
