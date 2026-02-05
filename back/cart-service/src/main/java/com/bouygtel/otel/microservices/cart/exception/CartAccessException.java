package com.bouygtel.otel.microservices.cart.exception;

public class CartAccessException extends RuntimeException {

    public CartAccessException(String message) {
        super(message);
    }

    public CartAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
