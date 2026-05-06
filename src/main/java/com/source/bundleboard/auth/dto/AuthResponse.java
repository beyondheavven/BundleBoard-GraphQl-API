package com.source.bundleboard.auth.dto;

import com.source.bundleboard.user.dto.UserDto;

public record AuthResponse(

        String accessToken,

        String refreshToken,

        UserDto user,

        String error,

        Boolean isNew
) {
}
