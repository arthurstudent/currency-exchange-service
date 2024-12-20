package com.demo.currencyexchangeservice.api;

import com.demo.currencyexchangeservice.base.BaseIntegrationTest;
import com.demo.currencyexchangeservice.domain.enums.EnumCurrency;
import com.demo.currencyexchangeservice.dto.response.ExchangeResponseDto;
import com.demo.currencyexchangeservice.service.rates.ExchangeProcessorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ExchangeIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeProcessorService exchangeProcessorService;

    @Test
    void testConvertCurrencyWithValidInputs() throws Exception {
        EnumCurrency sourceCurrency = EnumCurrency.USD;
        List<String> targetCurrencies = List.of("EUR", "GBP");
        List<EnumCurrency> targetCurrencyList = List.of(EnumCurrency.EUR, EnumCurrency.GBP);
        BigDecimal amount = BigDecimal.valueOf(100);

        ExchangeResponseDto mockExchangeResponse = new ExchangeResponseDto()
                .setBase(sourceCurrency)
                .setAmount(amount)
                .setRates(Map.of(
                        EnumCurrency.EUR, BigDecimal.valueOf(85),
                        EnumCurrency.GBP, BigDecimal.valueOf(75)
                ));

        when(exchangeProcessorService.calculateExchangeRate(amount, sourceCurrency, targetCurrencyList))
                .thenReturn(mockExchangeResponse);

        mockMvc.perform(get("/v1/exchange")
                        .param("source", sourceCurrency.name())
                        .param("targets", String.join(",", targetCurrencies))
                        .param("amount", amount.toPlainString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.base").value(sourceCurrency.name()))
                .andExpect(jsonPath("$.data.amount").value(amount.toPlainString()))
                .andExpect(jsonPath("$.data.rates.EUR").value(85))
                .andExpect(jsonPath("$.data.rates.GBP").value(75));

        verify(exchangeProcessorService, times(1)).calculateExchangeRate(amount, sourceCurrency, targetCurrencyList);
    }
}