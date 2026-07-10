package com.source.bundleboard.api.exception;

public class UserAlreadyExistsException extends ApiException{

    public UserAlreadyExistsException(){
        super("User with email or username already exists.");
    }

}
