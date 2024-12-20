package com.demo.currencyexchangeservice.service.client;

import com.demo.currencyexchangeservice.dto.CurrencyRatesDto;
import com.demo.currencyexchangeservice.dto.GetCurrencyExchangeDto;
import lombok.NonNull;

public interface ApiLayerClient {
    CurrencyRatesDto getCurrencyExchangeRate(@NonNull GetCurrencyExchangeDto getCurrencyExchangeDto);
}
