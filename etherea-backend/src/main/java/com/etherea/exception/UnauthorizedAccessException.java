package com.etherea.exception;

public class UnauthorizedAccessException extends RuntimeException {

    // Default constructor
    public UnauthorizedAccessException() {
        super("Accès non autorisé.");
    }

    // Constructor with personalized message
    public UnauthorizedAccessException(String message) {
        super(message);
    }

    // Constructor with message and cause
    public UnauthorizedAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor with cause only
    public UnauthorizedAccessException(Throwable cause) {
        super(cause);
    }
}
