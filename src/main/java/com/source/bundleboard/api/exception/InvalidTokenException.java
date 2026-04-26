package com.source.bundleboard.api.exception;

public class InvalidTokenException extends RuntimeException{

    public InvalidTokenException(){
        super("Token expired or revoked.");
    }

}
