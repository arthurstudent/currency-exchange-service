package com.demo.currencyexchangeservice.service.rates;

import com.demo.currencyexchangeservice.base.BaseServiceTest;
import com.demo.currencyexchangeservice.dao.CurrencyRepository;
import com.demo.currencyexchangeservice.domain.enums.EnumCurrency;
import com.demo.currencyexchangeservice.dto.CurrencyRatesDto;
import com.demo.currencyexchangeservice.exceptions.DataNotFoundException;
import com.demo.currencyexchangeservice.service.rates.impl.RateProcessorServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RateProcessorServiceTest extends BaseServiceTest {

    @Mock
    private RateFetcherService rateFetcherService;

    @Mock
    private CurrencyRepository currencyRepository;

    @InjectMocks
    private RateProcessorServiceImpl rateProcessorService;

    @Test
    void testGetSpecificRates_Success() {
        EnumCurrency baseCurrency = EnumCurrency.USD;
        List<EnumCurrency> targetCurrencies = List.of(EnumCurrency.EUR, EnumCurrency.GBP);

        CurrencyRatesDto fetchedRates = new CurrencyRatesDto()
                .setSourceCurrency(baseCurrency)
                .setRates(Map.of(
                        EnumCurrency.EUR, BigDecimal.valueOf(0.85),
                        EnumCurrency.GBP, BigDecimal.valueOf(0.75),
                        EnumCurrency.CAD, BigDecimal.valueOf(1.25)
                ))
                .setTimestamp(LocalDateTime.now());

        when(currencyRepository.existsByCurrency(baseCurrency)).thenReturn(true);
        when(rateFetcherService.getAllRatesForBaseCurrency(baseCurrency)).thenReturn(fetchedRates);

        CurrencyRatesDto result = rateProcessorService.getSpecificRates(baseCurrency, targetCurrencies);

        assertNotNull(result);
        assertEquals(baseCurrency, result.getSourceCurrency());
        assertEquals(2, result.getRates().size());
        assertTrue(result.getRates().containsKey(EnumCurrency.EUR));
        assertTrue(result.getRates().containsKey(EnumCurrency.GBP));
        verify(currencyRepository).existsByCurrency(baseCurrency);
        verify(rateFetcherService).getAllRatesForBaseCurrency(baseCurrency);
    }

    @Test
    void testGetSpecificRates_BaseCurrencyNotRegistered() {
        EnumCurrency baseCurrency = EnumCurrency.USD;
        List<EnumCurrency> targetCurrencies = List.of(EnumCurrency.EUR, EnumCurrency.GBP);

        when(currencyRepository.existsByCurrency(baseCurrency)).thenReturn(false);

        assertThrows(DataNotFoundException.class, () -> rateProcessorService.getSpecificRates(baseCurrency, targetCurrencies));
        verify(currencyRepository).existsByCurrency(baseCurrency);
        verifyNoInteractions(rateFetcherService);
    }

    @Test
    void testGetSpecificRates_TargetCurrenciesEmpty() {
        EnumCurrency baseCurrency = EnumCurrency.USD;
        List<EnumCurrency> targetCurrencies = Collections.emptyList();

        CurrencyRatesDto fetchedRates = new CurrencyRatesDto()
                .setSourceCurrency(baseCurrency)
                .setRates(Map.of(
                        EnumCurrency.EUR, BigDecimal.valueOf(0.85),
                        EnumCurrency.GBP, BigDecimal.valueOf(0.75),
                        EnumCurrency.CAD, BigDecimal.valueOf(1.25)
                ))
                .setTimestamp(LocalDateTime.now());

        when(currencyRepository.existsByCurrency(baseCurrency)).thenReturn(true);
        when(rateFetcherService.getAllRatesForBaseCurrency(baseCurrency)).thenReturn(fetchedRates);

        CurrencyRatesDto result = rateProcessorService.getSpecificRates(baseCurrency, targetCurrencies);

        assertNotNull(result);
        assertEquals(baseCurrency, result.getSourceCurrency());
        assertEquals(result.getRates().size(), fetchedRates.getRates().size());
        verify(currencyRepository).existsByCurrency(baseCurrency);
        verify(rateFetcherService).getAllRatesForBaseCurrency(baseCurrency);
    }
}