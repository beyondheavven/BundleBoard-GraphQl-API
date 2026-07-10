package com.source.bundleboard.api.exception;

public class UserNotFoundException extends ApiException{

    public UserNotFoundException(){
        super("User not found");
    }
}
