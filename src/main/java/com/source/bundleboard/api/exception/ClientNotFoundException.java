package com.source.bundleboard.api.exception;

public class ClientNotFoundException extends ApiException{

    public ClientNotFoundException(){
        super("Client not found.");
    }

    public ClientNotFoundException(String details){
        super("Client not found.", details);
    }

}
