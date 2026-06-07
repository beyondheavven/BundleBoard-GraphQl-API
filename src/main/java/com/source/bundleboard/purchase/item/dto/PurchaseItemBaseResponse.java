package com.source.bundleboard.purchase.item.dto;

import com.source.bundleboard.collection.dto.CollectionShortResponse;

import java.math.BigDecimal;


public record PurchaseItemBaseResponse(
        Long id,

        BigDecimal snapshotPrice,

        Long collectionId,

        CollectionShortResponse asset
) {
}
