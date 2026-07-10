package com.source.bundleboard.api.exception;

public class DescriptionException extends ApiException{
    public DescriptionException(String message){
        super(message);
    }

    public DescriptionException(String message, String details){
        super("Description", details);
    }
}
