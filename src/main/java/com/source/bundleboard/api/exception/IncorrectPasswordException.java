package com.source.bundleboard.api.exception;

public class IncorrectPasswordException extends RuntimeException{

    public IncorrectPasswordException(String message){
        super(message);
    }
}
