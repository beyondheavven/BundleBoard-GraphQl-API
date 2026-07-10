package com.source.bundleboard.api.exception;

public class BadTemplateEmailException extends ApiException {
    public BadTemplateEmailException() {
        super("Bad template email.");
    }

    public BadTemplateEmailException(String details) {
        super("Bad template email.", details);
    }
}
