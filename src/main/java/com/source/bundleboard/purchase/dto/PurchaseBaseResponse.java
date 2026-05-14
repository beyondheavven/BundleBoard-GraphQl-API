package com.source.bundleboard.purchase.dto;

import com.source.bundleboard.collection.dto.CollectionShortResponse;
import com.source.bundleboard.purchase.model.PurchaseStatus;

import java.time.Instant;

public record PurchaseBaseResponse(

        Long id,

        Double amount,

        String currency,

        PurchaseStatus status,

        Double snapshotPrice,

        Instant createdAt,

        CollectionShortResponse asset
) {
}
