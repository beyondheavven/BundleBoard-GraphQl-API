package com.source.bundleboard.user.dto;

import com.source.bundleboard.purchase.dto.PurchaseBaseResponse;
import com.source.bundleboard.user.model.UserStatus;

import java.util.List;

public record UserProfileResponse(
        Long id,

        String username,

        String email,

        String avatarUrl,

        UserStatus status,

        List<PurchaseBaseResponse> purchases
) {
}
