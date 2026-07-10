package com.source.bundleboard.api.exception;

public class UserStatusException extends ApiException{

    public UserStatusException(){
        super("User is banned or inactive.");
    }

}
