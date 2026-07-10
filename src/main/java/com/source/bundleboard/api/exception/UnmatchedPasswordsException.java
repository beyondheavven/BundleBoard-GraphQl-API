package com.source.bundleboard.api.exception;

public class UnmatchedPasswordsException extends ApiException {
    public UnmatchedPasswordsException() {
        super("Entered passwords do not match.");
    }
}
