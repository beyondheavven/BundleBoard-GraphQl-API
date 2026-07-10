package com.source.bundleboard.api.exception;

public class InvalidEmailVerificationTokenException extends ApiException {
    public InvalidEmailVerificationTokenException() {
        super("Invalid or expired token.");
    }
}
