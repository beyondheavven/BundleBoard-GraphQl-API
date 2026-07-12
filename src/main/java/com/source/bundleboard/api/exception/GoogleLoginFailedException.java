package com.source.bundleboard.api.exception;

public class GoogleLoginFailedException extends ApiException{

    public GoogleLoginFailedException(){
        super("Google login failed.");
    }

    public GoogleLoginFailedException(String details){
        super("Google login failed.", details);
    }
}
