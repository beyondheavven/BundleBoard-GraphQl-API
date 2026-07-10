package com.source.bundleboard.api.exception;

public class InvalidTokenException extends ApiException{

    public InvalidTokenException(){
        super("Token expired or revoked.");
    }

}
