package com.source.bundleboard.api.exception;

public class RegistrationFailedException extends ApiException{

    public RegistrationFailedException(){
        super("Registration failed.");
    }

    public RegistrationFailedException(String details){
        super("Registration failed.", details);
    }


}
