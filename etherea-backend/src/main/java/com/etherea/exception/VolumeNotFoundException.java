package com.etherea.exception;

public class VolumeNotFoundException extends RuntimeException {
    public VolumeNotFoundException(String message) {
        super(message);
    }
}