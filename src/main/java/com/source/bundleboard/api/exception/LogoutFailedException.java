package com.source.bundleboard.api.exception;

public class LogoutFailedException extends ApiException {

    public LogoutFailedException() {
        super("Logout failed.");
    }

    public LogoutFailedException(String details) {
        super("Logout failed.", details);
    }
}
