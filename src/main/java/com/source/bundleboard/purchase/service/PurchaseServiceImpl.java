package com.source.bundleboard.purchase.service;

import com.source.bundleboard.collection.service.CollectionService;
import com.source.bundleboard.purchase.dto.PurchaseBaseResponse;
import com.source.bundleboard.purchase.item.mapper.PurchaseItemMapper;
import com.source.bundleboard.purchase.item.model.PurchaseItem;
import com.source.bundleboard.purchase.item.service.PurchaseItemService;
import com.source.bundleboard.purchase.mapper.PurchaseMapper;
import com.source.bundleboard.purchase.model.Purchase;
import com.source.bundleboard.purchase.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final CollectionService collectionService;

    private final PurchaseMapper purchaseMapper;

    private final PurchaseRepository purchaseRepository;

    private final PurchaseItemService purchaseItemService;

    private final PurchaseItemMapper purchaseItemMapper;

    @Override
    public Mono<List<PurchaseBaseResponse>> findAllByUserId(Long userId) {
        return purchaseRepository.findAllByUserId(userId)
                .flatMap(this::enrichPurchaseWithAsset)
                .collectList();
    }

    @Override
    public Mono<Purchase> findByStripeSessionId(String stripeSessionId) {
        return purchaseRepository.findByStripeSessionId(stripeSessionId);
    }

    @Override
    public Mono<Purchase> findByStripePaymentIntentId(String stripePaymentIntentId) {
        return purchaseRepository.findByStripePaymentIntentId(stripePaymentIntentId);
    }

    @Override
    public Mono<Purchase> save(Purchase purchase) {
        return purchaseRepository.save(purchase);
    }

    @Override
    public Mono<Purchase> createPurchaseWithItems(Purchase purchase, List<PurchaseItem> items) {
        return purchaseRepository.save(purchase)
                .flatMap(savedPurchase -> {
                    items.forEach(item -> item.setPurchaseId(savedPurchase.getId()));
                    return purchaseItemService.saveAll(items).then().thenReturn(savedPurchase);
                });
    }

    private Mono<PurchaseBaseResponse> enrichPurchaseWithAsset(Purchase purchase) {
        return purchaseItemService.findAllByPurchaseId(purchase.getId())
                .flatMap(item ->
                        collectionService.findShortResponseById(item.getCollectionId())
                                .map(collectionMin -> purchaseItemMapper.toItemResponse(item, collectionMin))
                )
                .collectList()
                .map(enrichedItems ->
                        purchaseMapper.toBaseResponse(purchase, enrichedItems)
                );
    }
}
