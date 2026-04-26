package com.source.bundleboard.api.exception;

public class UserStatusException extends RuntimeException{

    public UserStatusException(){
        super("User is banned or inactive.");
    }

}
