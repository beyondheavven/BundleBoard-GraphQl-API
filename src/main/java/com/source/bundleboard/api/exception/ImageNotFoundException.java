package com.source.bundleboard.api.exception;


public class ImageNotFoundException extends ApiException{
    public ImageNotFoundException(){
        super("Image not found.");
    }
}
