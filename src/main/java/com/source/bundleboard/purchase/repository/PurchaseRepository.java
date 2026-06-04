package com.source.bundleboard.purchase.repository;

import com.source.bundleboard.purchase.model.Purchase;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PurchaseRepository extends R2dbcRepository<Purchase, Long> {

     Flux<Purchase> findAllByUserId(Long clientId);

    Mono<Purchase> findByStripePaymentIntentId(String stripePaymentIntentId);

    Mono<Purchase> findByStripeSessionId(String stripeSessionId);
}
