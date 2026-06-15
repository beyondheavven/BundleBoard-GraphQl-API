package com.source.bundleboard.purchase.dto;

import com.source.bundleboard.purchase.model.PurchaseStatus;

public record DownloadVerificationResponse(

        PurchaseStatus status,

        String assetPath
) {
}
