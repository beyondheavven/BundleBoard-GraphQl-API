package com.source.bundleboard.api.exception;

public class ClientNotFoundException extends ApiException{

    public ClientNotFoundException(){
        super("Client not found.");
    }

    public ClientNotFoundException(Long userId) {
        super("Client not found for userId: " + userId);
    }

}
