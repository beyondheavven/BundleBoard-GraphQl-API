package com.source.bundleboard.user.dto;

import com.source.bundleboard.author.dto.SocialLink;
import com.source.bundleboard.collection.dto.AuthoredCollectionResponse;
import com.source.bundleboard.purchase.dto.PurchaseBaseResponse;
import com.source.bundleboard.user.model.UserRole;
import com.source.bundleboard.user.model.UserStatus;

import java.util.List;
import java.util.Set;

public record UserProfileResponse(
        Long id,

        String username,

        String email,

        String avatarUrl,

        UserStatus status,

        Set<UserRole> roles,

        List<AuthoredCollectionResponse> authoredCollections,

        List<PurchaseBaseResponse> purchases,

        String bio,

        List<SocialLink> socialLinks,

        String stripeAccountId
) {
}
