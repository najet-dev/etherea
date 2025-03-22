package com.etherea.exception;

public class PaymentMethodNotFoundException extends RuntimeException {
    public PaymentMethodNotFoundException(String message) {
        super(message);
    }
}
