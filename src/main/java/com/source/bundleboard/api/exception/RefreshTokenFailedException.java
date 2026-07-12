package com.source.bundleboard.api.exception;

public class RefreshTokenFailedException extends ApiException{

    public RefreshTokenFailedException() {
        super("Refresh token failed.");
    }

    public RefreshTokenFailedException(String details) {
        super("Refresh token failed.", details);
    }

}
