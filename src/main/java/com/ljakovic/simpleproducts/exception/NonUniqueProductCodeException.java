package com.ljakovic.simpleproducts.exception;

public class NonUniqueProductCodeException extends RuntimeException{

    public NonUniqueProductCodeException(String message) {
        super(message);
    }

    public NonUniqueProductCodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
