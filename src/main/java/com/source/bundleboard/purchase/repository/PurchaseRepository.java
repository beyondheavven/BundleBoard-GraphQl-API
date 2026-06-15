package com.source.bundleboard.purchase.repository;

import com.source.bundleboard.purchase.model.Purchase;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PurchaseRepository extends R2dbcRepository<Purchase, Long> {

     Flux<Purchase> findAllByUserId(Long clientId);

    Mono<Purchase> findByStripePaymentIntentId(String stripePaymentIntentId);

    Mono<Purchase> findByStripeSessionId(String stripeSessionId);

    @Query("SELECT p.* FROM purchases p " +
            "JOIN purchase_items pi ON p.id = pi.purchase_id " +
            "WHERE p.user_id = :userId AND pi.collection_id = :collectionId " +
            "LIMIT 1")
    Mono<Purchase> findByUserIdAndCollectionId(Long userId, Long collectionId);
}
