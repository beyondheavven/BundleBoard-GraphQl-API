package com.source.bundleboard.purchase;

import com.source.bundleboard.collection.dto.CollectionShortResponse;
import com.source.bundleboard.collection.service.CollectionService;
import com.source.bundleboard.purchase.dto.PurchaseBaseResponse;
import com.source.bundleboard.purchase.item.dto.PurchaseItemBaseResponse;
import com.source.bundleboard.purchase.item.mapper.PurchaseItemMapper;
import com.source.bundleboard.purchase.item.model.PurchaseItem;
import com.source.bundleboard.purchase.item.repository.PurchaseItemRepository;
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

import java.math.BigDecimal;
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
    private PurchaseItemMapper purchaseItemMapper;

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private PurchaseItemRepository purchaseItemRepository;

    @InjectMocks
    private PurchaseServiceImpl purchaseService;

    private Purchase purchase1;
    private Purchase purchase2;
    private PurchaseItem item1;
    private PurchaseItem item2;
    private CollectionShortResponse mockAsset;
    private PurchaseItemBaseResponse itemResponse1;
    private PurchaseItemBaseResponse itemResponse2;
    private PurchaseBaseResponse response1;
    private PurchaseBaseResponse response2;

    @BeforeEach
    void setUp() {
        Instant now = Instant.now();

        purchase1 = new Purchase();
        purchase1.setId(1L);
        purchase1.setUserId(100L);
        purchase1.setAmount(new BigDecimal("99.99"));
        purchase1.setCurrency("USD");
        purchase1.setStatus(PurchaseStatus.succeeded);
        purchase1.setCreatedAt(now);

        purchase2 = new Purchase();
        purchase2.setId(2L);
        purchase2.setUserId(100L);
        purchase2.setAmount(new BigDecimal("49.99"));
        purchase2.setCurrency("USD");
        purchase2.setStatus(PurchaseStatus.succeeded);
        purchase2.setCreatedAt(now);

        item1 = new PurchaseItem();
        item1.setId(11L);
        item1.setPurchaseId(1L);
        item1.setCollectionId(501L);
        item1.setSnapshotPrice(new BigDecimal("99.99"));

        item2 = new PurchaseItem();
        item2.setId(12L);
        item2.setPurchaseId(2L);
        item2.setCollectionId(502L);
        item2.setSnapshotPrice(new BigDecimal("49.99"));

        mockAsset = new CollectionShortResponse(501L, "Test Collection");

        itemResponse1 = new PurchaseItemBaseResponse(11L, new BigDecimal("99.99"), mockAsset);
        itemResponse2 = new PurchaseItemBaseResponse(12L, new BigDecimal("49.99"), mockAsset);

        response1 = new PurchaseBaseResponse(
                1L,
                new BigDecimal("99.99"),
                "USD",
                PurchaseStatus.succeeded,
                now,
                List.of(itemResponse1)
        );

        response2 = new PurchaseBaseResponse(
                2L,
                new BigDecimal("49.99"),
                "USD",
                PurchaseStatus.succeeded,
                now,
                List.of(itemResponse2)
        );
    }

    @Test
    void findAllByUserId_Success_ReturnsEnrichedList() {
        Long userId = 100L;

        when(purchaseRepository.findAllByUserId(userId))
                .thenReturn(Flux.just(purchase1, purchase2));

        when(purchaseItemRepository.findAllByPurchaseId(1L)).thenReturn(Flux.just(item1));
        when(purchaseItemRepository.findAllByPurchaseId(2L)).thenReturn(Flux.just(item2));
        when(collectionService.findShortResponseById(501L)).thenReturn(Mono.just(mockAsset));
        when(collectionService.findShortResponseById(502L)).thenReturn(Mono.just(mockAsset));

        when(purchaseItemMapper.toItemResponse(eq(item1), any(CollectionShortResponse.class))).thenReturn(itemResponse1);
        when(purchaseItemMapper.toItemResponse(eq(item2), any(CollectionShortResponse.class))).thenReturn(itemResponse2);
        when(purchaseMapper.toBaseResponse(eq(purchase1), eq(List.of(itemResponse1)))).thenReturn(response1);
        when(purchaseMapper.toBaseResponse(eq(purchase2), eq(List.of(itemResponse2)))).thenReturn(response2);

        Mono<List<PurchaseBaseResponse>> result = purchaseService.findAllByUserId(userId);

        StepVerifier.create(result)
                .assertNext(list -> {
                    assertNotNull(list);
                    assertEquals(2, list.size());

                    PurchaseBaseResponse first = list.get(0);
                    assertEquals(1L, first.id());
                    assertEquals("USD", first.currency());
                    assertEquals(PurchaseStatus.succeeded, first.status());
                    assertFalse(first.items().isEmpty());
                    assertEquals(mockAsset, first.items().get(0).asset());

                    assertEquals(response2, list.get(1));
                })
                .verifyComplete();

        verify(purchaseRepository, times(1)).findAllByUserId(userId);
        verify(purchaseRepository, times(1)).findAllByUserId(1L);
        verify(purchaseRepository, times(1)).findAllByUserId(2L);
        verify(collectionService, times(1)).findShortResponseById(501L);
        verify(collectionService, times(1)).findShortResponseById(502L);
        verify(purchaseItemMapper, times(2)).toItemResponse(any(), any());
        verify(purchaseMapper, times(2)).toBaseResponse(any(), anyList());
    }

    @Test
    void findAllByUserId_NoPurchases_ReturnsEmptyList() {
        Long userId = 100L;

        when(purchaseRepository.findAllByUserId(userId)).thenReturn(Flux.empty());

        Mono<List<PurchaseBaseResponse>> result = purchaseService.findAllByUserId(userId);

        StepVerifier.create(result)
                .assertNext(list -> {
                    assertNotNull(list);
                    assertTrue(list.isEmpty());
                })
                .verifyComplete();

        verify(purchaseRepository, times(1)).findAllByUserId(userId);
        verifyNoInteractions(purchaseItemRepository);
        verifyNoInteractions(collectionService);
        verifyNoInteractions(purchaseMapper);
    }
}