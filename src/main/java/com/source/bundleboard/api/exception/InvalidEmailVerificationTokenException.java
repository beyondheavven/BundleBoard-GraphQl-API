package com.source.bundleboard.api.exception;

public class InvalidEmailVerificationTokenException extends RuntimeException {
    public InvalidEmailVerificationTokenException() {
        super("Invalid or expired token.");
    }
}
