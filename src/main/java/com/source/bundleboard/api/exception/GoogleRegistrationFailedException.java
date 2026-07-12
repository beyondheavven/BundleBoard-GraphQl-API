package com.source.bundleboard.api.exception;

public class GoogleRegistrationFailedException extends ApiException{

    public GoogleRegistrationFailedException(){
        super("Google registration failed.");
    }

    public GoogleRegistrationFailedException(String details){
        super("Google registration failed.", details);
    }
}
