package com.demo.currencyexchangeservice.commons;

import com.demo.currencyexchangeservice.domain.enums.EnumCurrency;
import com.demo.currencyexchangeservice.exceptions.UnknownCurrencyTypeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyUtilsTest {

    @ParameterizedTest
    @ValueSource(strings = {"USD", "EUR", "UAH", "GBP"})
    void testWrapEnumCurrency(String currency) {
        EnumCurrency enumCurrency = CurrencyUtils.wrapEnumCurrency(currency);
        assertEquals(EnumCurrency.valueOf(currency), enumCurrency);
    }

    @Test
    void testWrapEnumCurrencyThrowsException() {
        assertThrows(UnknownCurrencyTypeException.class, () -> CurrencyUtils.wrapEnumCurrency("test data"));
    }
}