package com.source.bundleboard.api.exception;

public class UnmatchedPasswordsException extends RuntimeException {
    public UnmatchedPasswordsException() {
        super("Entered passwords do not match.");
    }
}
