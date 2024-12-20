package com.demo.currencyexchangeservice.service.rates;

import com.demo.currencyexchangeservice.base.BaseServiceTest;
import com.demo.currencyexchangeservice.domain.enums.EnumCurrency;
import com.demo.currencyexchangeservice.dto.CurrencyRatesDto;
import com.demo.currencyexchangeservice.dto.response.ExchangeResponseDto;
import com.demo.currencyexchangeservice.exceptions.DataNotFoundException;
import com.demo.currencyexchangeservice.service.rates.impl.ExchangeProcessorServiceImpl;
import com.demo.currencyexchangeservice.service.rates.RateProcessorService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExchangeProcessorServiceTest extends BaseServiceTest {

    @Mock
    private RateProcessorService rateProcessorService;

    @InjectMocks
    private ExchangeProcessorServiceImpl exchangeProcessorService;

    @Test
    void calculateExchangeRate_ShouldReturnValidResponse() {
        EnumCurrency baseCurrency = EnumCurrency.USD;
        List<EnumCurrency> targetCurrencies = List.of(EnumCurrency.EUR, EnumCurrency.GBP);

        BigDecimal amount = BigDecimal.valueOf(100d);
        BigDecimal eurAmount = BigDecimal.valueOf(0.85d);
        BigDecimal gbpAmount = BigDecimal.valueOf(0.75d);

        Map<EnumCurrency, BigDecimal> mockRates = Map.of(
                EnumCurrency.EUR, eurAmount,
                EnumCurrency.GBP, gbpAmount
        );

        CurrencyRatesDto mockRatesDto = new CurrencyRatesDto()
                .setRates(mockRates)
                .setSourceCurrency(baseCurrency);

        when(rateProcessorService.getSpecificRates(baseCurrency, targetCurrencies)).thenReturn(mockRatesDto);

        ExchangeResponseDto result = exchangeProcessorService.calculateExchangeRate(amount, baseCurrency, targetCurrencies);

        Map<EnumCurrency, BigDecimal> rates = result.getRates();

        assertEquals(baseCurrency, result.getBase());
        assertEquals(amount, result.getAmount());
        assertNotNull(rates);
        assertEquals(2, rates.size());
        assertTrue(rates.containsKey(EnumCurrency.EUR));
        assertTrue(rates.containsKey(EnumCurrency.GBP));
        assertEquals(BigDecimal.valueOf(85d), rates.get(EnumCurrency.EUR));
        assertEquals(BigDecimal.valueOf(75d), rates.get(EnumCurrency.GBP));
        verify(rateProcessorService).getSpecificRates(baseCurrency, targetCurrencies);
    }

    @Test
    void calculateExchangeRate_ShouldHandleEmptyTargets() {
        EnumCurrency baseCurrency = EnumCurrency.USD;

        BigDecimal amount = BigDecimal.valueOf(100d);
        BigDecimal eurAmount = BigDecimal.valueOf(0.85d);
        BigDecimal gbpAmount = BigDecimal.valueOf(0.75d);

        List<EnumCurrency> targetCurrencies = Collections.emptyList();
        Map<EnumCurrency, BigDecimal> mockRates = Map.of(
                EnumCurrency.EUR, eurAmount,
                EnumCurrency.GBP, gbpAmount
        );

        CurrencyRatesDto mockRatesDto = new CurrencyRatesDto()
                .setRates(mockRates)
                .setSourceCurrency(baseCurrency);

        when(rateProcessorService.getSpecificRates(baseCurrency, targetCurrencies)).thenReturn(mockRatesDto);

        ExchangeResponseDto result = exchangeProcessorService.calculateExchangeRate(amount, baseCurrency, targetCurrencies);

        assertEquals(2, result.getRates().size());
        assertTrue(result.getRates().containsKey(EnumCurrency.EUR));
        assertTrue(result.getRates().containsKey(EnumCurrency.GBP));
    }

    @Test
    void calculateExchangeRate_ShouldHandleLargeAmounts() {
        EnumCurrency baseCurrency = EnumCurrency.USD;

        BigDecimal amount = BigDecimal.valueOf(1000000000d);
        BigDecimal eurAmount = BigDecimal.valueOf(0.85d);

        List<EnumCurrency> targetCurrencies = List.of(EnumCurrency.EUR);
        Map<EnumCurrency, BigDecimal> mockRates = Map.of(EnumCurrency.EUR, eurAmount);

        CurrencyRatesDto mockRatesDto = new CurrencyRatesDto()
                .setRates(mockRates)
                .setSourceCurrency(baseCurrency);

        when(rateProcessorService.getSpecificRates(baseCurrency, targetCurrencies)).thenReturn(mockRatesDto);

        ExchangeResponseDto result = exchangeProcessorService.calculateExchangeRate(amount, baseCurrency, targetCurrencies);

        assertEquals(BigDecimal.valueOf(850000000d), result.getRates().get(EnumCurrency.EUR));
    }

    @Test
    void calculateExchangeRate_ShouldHandleExceptionsFromDependency() {
        EnumCurrency baseCurrency = EnumCurrency.USD;
        BigDecimal amount = BigDecimal.valueOf(100);
        List<EnumCurrency> targetCurrencies = List.of(EnumCurrency.EUR);

        when(rateProcessorService.getSpecificRates(baseCurrency, targetCurrencies)).thenThrow(new DataNotFoundException("Base currency not found"));

        DataNotFoundException exception = assertThrows(DataNotFoundException.class, () ->
                exchangeProcessorService.calculateExchangeRate(amount, baseCurrency, targetCurrencies)
        );
        assertEquals("Base currency not found", exception.getMessage());
    }
}