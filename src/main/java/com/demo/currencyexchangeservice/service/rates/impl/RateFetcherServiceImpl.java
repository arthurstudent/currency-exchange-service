package com.demo.currencyexchangeservice.service.rates.impl;

import com.demo.currencyexchangeservice.domain.enums.EnumCurrency;
import com.demo.currencyexchangeservice.dto.CurrencyRatesDto;
import com.demo.currencyexchangeservice.dto.GetCurrencyExchangeDto;
import com.demo.currencyexchangeservice.service.client.ApiLayerClient;
import com.demo.currencyexchangeservice.service.rates.RateFetcherService;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Log4j2
@CacheConfig(cacheNames = {"currenciesRates"})
@Service
@RequiredArgsConstructor
public class RateFetcherServiceImpl implements RateFetcherService {

    private final ApiLayerClient apiLayerClient;

    @Cacheable(key = "#baseCurrency")
    @Override
    public CurrencyRatesDto getAllRatesForBaseCurrency(@NonNull EnumCurrency baseCurrency) {
        log.info("Fetching rates for base currency {} from API", baseCurrency);
        var getCurrencyExchangeDto = new GetCurrencyExchangeDto(baseCurrency);
        return apiLayerClient.getCurrencyExchangeRate(getCurrencyExchangeDto);
    }

    @CacheEvict(allEntries = true)
    @PostConstruct
    @Override
    public void clearCache() {
        log.info("Caches are cleared");
    }
}
