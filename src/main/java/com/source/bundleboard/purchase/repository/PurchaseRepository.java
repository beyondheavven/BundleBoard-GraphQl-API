package com.source.bundleboard.purchase.repository;

import com.source.bundleboard.purchase.model.Purchase;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseRepository extends R2dbcRepository<Purchase, Long> {
}
