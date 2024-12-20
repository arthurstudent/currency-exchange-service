package com.demo.currencyexchangeservice.commons;

import com.demo.currencyexchangeservice.domain.enums.EnumCurrency;
import com.demo.currencyexchangeservice.exceptions.UnknownCurrencyTypeException;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

@Log4j2
@UtilityClass
public class CurrencyUtils {

    public static EnumCurrency wrapEnumCurrency(@NonNull String currency) {
        try {
            return EnumCurrency.valueOf(currency);
        } catch (IllegalArgumentException e) {
            log.warn("Unable to convert {} to an EnumCurrency", currency);
            throw new UnknownCurrencyTypeException("Invalid currency: " + currency);
        }
    }
}
