package com.demo.currencyexchangeservice.service.rates;

import com.demo.currencyexchangeservice.domain.enums.EnumCurrency;
import com.demo.currencyexchangeservice.dto.CurrencyRatesDto;
import lombok.NonNull;

import java.util.Collection;

public interface RateProcessorService {
    CurrencyRatesDto getSpecificRates(@NonNull EnumCurrency baseCurrency, @NonNull Collection<EnumCurrency> targetCurrencies);
}
