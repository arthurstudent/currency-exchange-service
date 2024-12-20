package com.demo.currencyexchangeservice.service.rates;

import com.demo.currencyexchangeservice.domain.enums.EnumCurrency;
import com.demo.currencyexchangeservice.dto.CurrencyRatesDto;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;

public interface RateFetcherService {

    CurrencyRatesDto getAllRatesForBaseCurrency(@NonNull EnumCurrency baseCurrency);

    @PostConstruct
    void clearCache();
}
