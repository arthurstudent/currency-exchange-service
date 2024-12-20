package com.demo.currencyexchangeservice.service.rates;

import com.demo.currencyexchangeservice.base.BaseIntegrationTest;
import com.demo.currencyexchangeservice.domain.enums.EnumCurrency;
import com.demo.currencyexchangeservice.dto.CurrencyRatesDto;
import com.demo.currencyexchangeservice.service.client.ApiLayerClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@EnableCaching
class RateFetcherServiceImplTest extends BaseIntegrationTest {

    @Autowired
    private RateFetcherService rateFetcherService;

    @MockBean
    private ApiLayerClient apiLayerClient;

    @Autowired
    private CacheManager cacheManager;

    @Value("${test.data.cache.name}")
    private String cacheName;

    @Test
    void getAllRatesForBaseCurrency_ShouldUseCache() {
        EnumCurrency baseCurrency = EnumCurrency.USD;
        CurrencyRatesDto expectedResponse = new CurrencyRatesDto()
                .setSourceCurrency(baseCurrency)
                .setRates(Map.of(EnumCurrency.EUR, BigDecimal.valueOf(1.1d), EnumCurrency.GBP, BigDecimal.valueOf(0.9)));

        when(apiLayerClient.getCurrencyExchangeRate(any())).thenReturn(expectedResponse);

        CurrencyRatesDto firstCallResult = rateFetcherService.getAllRatesForBaseCurrency(baseCurrency);

        Cache cache = cacheManager.getCache(cacheName);
        assertThat(cache).isNotNull();

        CurrencyRatesDto cachedResult = cache.get(baseCurrency, CurrencyRatesDto.class);
        assertThat(cachedResult).isEqualTo(firstCallResult);

        CurrencyRatesDto secondCallResult = rateFetcherService.getAllRatesForBaseCurrency(baseCurrency);

        verify(apiLayerClient, times(1)).getCurrencyExchangeRate(any());
        assertThat(secondCallResult).isEqualTo(firstCallResult);
    }

    @Test
    void clearCache_ShouldEvictAllCacheEntries() {
        EnumCurrency baseCurrency = EnumCurrency.USD;
        CurrencyRatesDto expectedResponse = new CurrencyRatesDto()
                .setSourceCurrency(baseCurrency)
                .setRates(Map.of(EnumCurrency.EUR, BigDecimal.valueOf(1.1), EnumCurrency.GBP, BigDecimal.valueOf(0.9)));

        when(apiLayerClient.getCurrencyExchangeRate(any())).thenReturn(expectedResponse);

        rateFetcherService.getAllRatesForBaseCurrency(baseCurrency);
        Cache cache = cacheManager.getCache(cacheName);

        assertThat(cache).isNotNull();
        assertThat(cache.get(baseCurrency)).isNotNull();

        rateFetcherService.clearCache();

        assertThat(cache.get(baseCurrency)).isNull();
    }
}