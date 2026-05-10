package com.source.bundleboard.api.exception;

public class AuthorNotFoundException extends RuntimeException{
    public AuthorNotFoundException(){
        super("Author not found.");
    }
}
