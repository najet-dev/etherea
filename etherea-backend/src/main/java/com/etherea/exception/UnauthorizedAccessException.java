package com.etherea.exception;

public class UnauthorizedAccessException extends RuntimeException {

    // Constructeur par défaut
    public UnauthorizedAccessException() {
        super("Accès non autorisé.");
    }

    // Constructeur avec message personnalisé
    public UnauthorizedAccessException(String message) {
        super(message);
    }

    // Constructeur avec message et cause
    public UnauthorizedAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructeur avec cause seulement
    public UnauthorizedAccessException(Throwable cause) {
        super(cause);
    }
}
