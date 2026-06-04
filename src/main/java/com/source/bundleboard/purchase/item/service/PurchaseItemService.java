package com.source.bundleboard.purchase.item.service;

import com.source.bundleboard.purchase.item.model.PurchaseItem;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PurchaseItemService {

    Mono<PurchaseItem> save(PurchaseItem purchaseItem);

    Flux<PurchaseItem> saveAll(List<PurchaseItem> items);

    Flux<PurchaseItem> findAllByPurchaseId(Long id);
}
