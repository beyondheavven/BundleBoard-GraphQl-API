package com.source.bundleboard.api.exception;


public class ImageNotFoundException extends RuntimeException{
    public ImageNotFoundException(){
        super("Image not found.");
    }
}
