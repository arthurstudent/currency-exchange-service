package com.demo.currencyexchangeservice.exceptions;

public class UnknownCurrencyTypeException extends RuntimeException {
    public UnknownCurrencyTypeException(String message) {
        super(message);
    }
}
