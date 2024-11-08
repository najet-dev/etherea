package com.etherea.exception;
public class GeocodingApiException extends RuntimeException {
    public GeocodingApiException(String message) {
        super(message);
    }
    public GeocodingApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
