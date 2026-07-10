package com.source.bundleboard.api.exception;

import lombok.Getter;

@Getter
public abstract class ApiException extends RuntimeException {

    private final String details;

    protected ApiException(String message) {
        super(message);
        this.details = message;
    }

    protected ApiException(String message, String details) {
        super(message);
        this.details = details;
    }

}
