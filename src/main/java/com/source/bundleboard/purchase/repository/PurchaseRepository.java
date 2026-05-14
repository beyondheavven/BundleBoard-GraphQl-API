package com.source.bundleboard.purchase.repository;

import com.source.bundleboard.purchase.model.Purchase;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface PurchaseRepository extends R2dbcRepository<Purchase, Long> {

     Flux<Purchase> findAllByClientId(Long clientId);
}
