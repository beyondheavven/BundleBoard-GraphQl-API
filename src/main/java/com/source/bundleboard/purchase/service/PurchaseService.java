package com.source.bundleboard.purchase.service;

import com.source.bundleboard.purchase.dto.PurchaseBaseResponse;
import com.source.bundleboard.purchase.item.model.PurchaseItem;
import com.source.bundleboard.purchase.model.Purchase;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PurchaseService {

    Mono<List<PurchaseBaseResponse>> findAllByUserId(Long clientId);

    Mono<Purchase> findByStripeSessionId(String stripePaymentIntentId);

    Mono<Purchase> findByStripePaymentIntentId(String id);

    Mono<Purchase> save(Purchase purchase);

    Mono<Purchase> createPurchaseWithItems(Purchase purchase, List<PurchaseItem> items);
}
