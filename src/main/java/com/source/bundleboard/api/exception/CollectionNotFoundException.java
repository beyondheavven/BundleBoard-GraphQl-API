package com.source.bundleboard.api.exception;

public class CollectionNotFoundException extends RuntimeException{

    public CollectionNotFoundException(){
        super("Collection not found");
    }
}
