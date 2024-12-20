package com.demo.currencyexchangeservice.api;

import com.demo.currencyexchangeservice.base.BaseIntegrationTest;
import com.demo.currencyexchangeservice.domain.enums.EnumCurrency;
import com.demo.currencyexchangeservice.dto.CurrencyRatesDto;
import com.demo.currencyexchangeservice.service.rates.RateProcessorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RatesIntegrationTest extends BaseIntegrationTest {

    @MockBean
    private RateProcessorService rateProcessorService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetRatesWithValidInputs() throws Exception {
        EnumCurrency sourceCurrency = EnumCurrency.USD;
        List<String> targetCurrencies = List.of("EUR", "GBP");
        List<EnumCurrency> targetCurrencyList = List.of(EnumCurrency.EUR, EnumCurrency.GBP);

        CurrencyRatesDto mockResponse = new CurrencyRatesDto()
                .setSourceCurrency(sourceCurrency)
                .setRates(Map.of(
                        EnumCurrency.EUR, BigDecimal.valueOf(0.85),
                        EnumCurrency.GBP, BigDecimal.valueOf(0.75)
                ))
                .setTimestamp(LocalDateTime.now());

        when(rateProcessorService.getSpecificRates(sourceCurrency, targetCurrencyList)).thenReturn(mockResponse);

        mockMvc.perform(get("/v1/exchange_rates")
                        .param("source", sourceCurrency.name())
                        .param("targets", String.join(",", targetCurrencies))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.sourceCurrency").value(sourceCurrency.name()))
                .andExpect(jsonPath("$.data.rates.EUR").value(0.85))
                .andExpect(jsonPath("$.data.rates.GBP").value(0.75));

        verify(rateProcessorService, times(1)).getSpecificRates(sourceCurrency, targetCurrencyList);
    }
}