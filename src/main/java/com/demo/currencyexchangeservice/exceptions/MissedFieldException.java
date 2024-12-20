package com.demo.currencyexchangeservice.exceptions;

public class MissedFieldException extends RuntimeException {

    public MissedFieldException(String message) {
        super(message);
    }
}
