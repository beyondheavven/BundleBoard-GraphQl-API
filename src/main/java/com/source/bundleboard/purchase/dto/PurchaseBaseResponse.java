package com.source.bundleboard.purchase.dto;

import com.source.bundleboard.purchase.item.dto.PurchaseItemBaseResponse;
import com.source.bundleboard.purchase.model.PurchaseStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record PurchaseBaseResponse(

        Long id,

        BigDecimal amount,

        String currency,

        PurchaseStatus status,

        Instant createdAt,

        List<PurchaseItemBaseResponse> items
) {
}
