package com.demo.currencyexchangeservice.exceptions;

public class InvalidResponseException extends RuntimeException{
    public InvalidResponseException(String message) {
        super(message);
    }
}
