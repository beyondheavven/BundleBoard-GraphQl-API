package com.source.bundleboard.api.exception;

public class MediaResourceNotFoundException extends ApiException{

    public MediaResourceNotFoundException(Long id){
        super("Media resource with ID " + id + " not found.");
    }
}
