package com.source.bundleboard.user.dto;

public record UpdateUserRoleResponse(

        String message,

        Boolean success,

        String accessToken,

        String refreshToken
) {
}
