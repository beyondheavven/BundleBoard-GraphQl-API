package com.source.bundleboard.api.exception;

public class AuthorNotFoundException extends ApiException {

    public AuthorNotFoundException(){
        super("Author not found.");
    }

    public AuthorNotFoundException(String details){
        super("Author not found.", details);
    }
}
