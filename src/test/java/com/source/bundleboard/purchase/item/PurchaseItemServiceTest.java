package com.source.bundleboard.purchase.item;

import com.source.bundleboard.purchase.item.model.PurchaseItem;
import com.source.bundleboard.purchase.item.repository.PurchaseItemRepository;
import com.source.bundleboard.purchase.item.service.PurchaseItemServiceImpl;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PurchaseItemServiceTest {

    @Mock
    private PurchaseItemRepository purchaseItemRepository;

    @InjectMocks
    private PurchaseItemServiceImpl purchaseItemService;

    private PurchaseItem item1;
    private PurchaseItem item2;
    private final Long purchaseId = 100L;

    @BeforeEach
    void setUp() {
        item1 = new PurchaseItem();
        item1.setId(1L);
        item1.setPurchaseId(purchaseId);
        item1.setCollectionId(501L);
        item1.setSnapshotPrice(new BigDecimal("19.99"));

        item2 = new PurchaseItem();
        item2.setId(2L);
        item2.setPurchaseId(purchaseId);
        item2.setCollectionId(502L);
        item2.setSnapshotPrice(new BigDecimal("29.99"));
    }

    // --- SAVE TESTS ---

    @Test
    void save_Success() {
        when(purchaseItemRepository.save(item1)).thenReturn(Mono.just(item1));

        StepVerifier.create(purchaseItemService.save(item1))
                .expectNext(item1)
                .verifyComplete();

        verify(purchaseItemRepository).save(item1);
    }

    @Test
    void saveAll_Success() {
        List<PurchaseItem> items = List.of(item1, item2);

        when(purchaseItemRepository.saveAll(items)).thenReturn(Flux.fromIterable(items));

        StepVerifier.create(purchaseItemService.saveAll(items))
                .expectNext(item1)
                .expectNext(item2)
                .verifyComplete();

        verify(purchaseItemRepository).saveAll(items);
    }

    // --- FIND TESTS ---

    @Test
    void findAllByPurchaseId_Success_MapsToDto() {
        when(purchaseItemRepository.findAllByPurchaseId(purchaseId)).thenReturn(Flux.just(item1, item2));

        StepVerifier.create(purchaseItemService.findAllByPurchaseId(purchaseId))
                .assertNext(response -> {
                    assertEquals(1L, response.id());
                    assertEquals(new BigDecimal("19.99"), response.snapshotPrice());
                    assertEquals(501L, response.collectionId());
                    assertNull(response.asset());
                })
                .assertNext(response -> {
                    assertEquals(2L, response.id());
                    assertEquals(new BigDecimal("29.99"), response.snapshotPrice());
                    assertEquals(502L, response.collectionId());
                    assertNull(response.asset());
                })
                .verifyComplete();

        verify(purchaseItemRepository).findAllByPurchaseId(purchaseId);
    }

    @Test
    void findAllByPurchaseId_EmptyResult_ReturnsEmptyFlux() {
        Long emptyPurchaseId = 999L;

        when(purchaseItemRepository.findAllByPurchaseId(emptyPurchaseId)).thenReturn(Flux.empty());

        StepVerifier.create(purchaseItemService.findAllByPurchaseId(emptyPurchaseId))
                .verifyComplete();

        verify(purchaseItemRepository).findAllByPurchaseId(emptyPurchaseId);
    }
}