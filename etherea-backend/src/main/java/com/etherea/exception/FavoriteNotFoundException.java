package com.etherea.exception;

public class FavoriteNotFoundException extends RuntimeException {
    public FavoriteNotFoundException(String message) {
        super(message);
    }
}