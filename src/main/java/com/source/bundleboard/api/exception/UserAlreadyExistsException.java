package com.source.bundleboard.api.exception;

public class UserAlreadyExistsException extends RuntimeException{

    public UserAlreadyExistsException(){
        super("Username already exists.");
    }

}
