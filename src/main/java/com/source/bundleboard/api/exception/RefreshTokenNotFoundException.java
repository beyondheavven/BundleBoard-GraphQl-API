package com.source.bundleboard.api.exception;

public class RefreshTokenNotFoundException extends ApiException {
    public RefreshTokenNotFoundException() {
        super("Refresh token not found.");
    }
}
