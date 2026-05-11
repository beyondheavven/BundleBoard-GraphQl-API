package com.source.bundleboard.api.exception;

public class MediaResourceNotFoundException extends RuntimeException{

    public MediaResourceNotFoundException(Long id){
        super("Media resource with ID " + id + " not found.");
    }
}
