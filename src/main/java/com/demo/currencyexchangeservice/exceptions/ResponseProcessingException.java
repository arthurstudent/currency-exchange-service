package com.demo.currencyexchangeservice.exceptions;

public class ResponseProcessingException extends RuntimeException {
    public ResponseProcessingException(String message) {
        super(message);
    }
}
