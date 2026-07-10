package com.source.bundleboard.api.exception;

public class CollectionNotFoundException extends ApiException {

    public CollectionNotFoundException(){
        super("Collection not found");
    }

    public CollectionNotFoundException(String details){
        super("Collection not found", details);
    }
}
