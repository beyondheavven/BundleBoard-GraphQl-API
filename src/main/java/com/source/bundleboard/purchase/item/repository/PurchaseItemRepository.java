package com.source.bundleboard.purchase.item.repository;

import com.source.bundleboard.purchase.item.model.PurchaseItem;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface PurchaseItemRepository extends R2dbcRepository<PurchaseItem, Long> {

    Flux<PurchaseItem> findAllByPurchaseId(Long PurchaseId);
}
