package com.demo.currencyexchangeservice.service.client.impl;

import com.demo.currencyexchangeservice.config.configurer.ExchangeApiConfigurer;
import com.demo.currencyexchangeservice.domain.enums.EnumCurrency;
import com.demo.currencyexchangeservice.dto.CurrencyRatesDto;
import com.demo.currencyexchangeservice.dto.GetCurrencyExchangeDto;
import com.demo.currencyexchangeservice.exceptions.InvalidResponseException;
import com.demo.currencyexchangeservice.exceptions.ResponseProcessingException;
import com.demo.currencyexchangeservice.service.client.ApiLayerClient;
import com.demo.currencyexchangeservice.service.parser.CurrencyRatesParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

@Log4j2
@Service
@RequiredArgsConstructor
public class ApiLayerClientImpl implements ApiLayerClient {

    private final OkHttpClient okHttpClient;

    private final ExchangeApiConfigurer exchangeApiConfigurer;

    private final CurrencyRatesParser currencyRatesParser;

    @Override
    public CurrencyRatesDto getCurrencyExchangeRate(@NonNull GetCurrencyExchangeDto getCurrencyExchangeDto) {
        EnumCurrency baseCurrency = Optional.ofNullable(getCurrencyExchangeDto.getBaseCurrency())
                .orElse(EnumCurrency.USD);

        String[] targetCurrencies = Stream.of(EnumCurrency.values())
                .map(EnumCurrency::name)
                .toArray(String[]::new);

        String symbols = String.join("%2C", targetCurrencies);

        String url = String.format("%s/live?source=%s&currencies=%s",
                exchangeApiConfigurer.getApiUrl(),
                baseCurrency.name(),
                symbols);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", exchangeApiConfigurer.getApiKey())
                .get()
                .build();

        try {
            String responseBody = executeRequest(request);
            return currencyRatesParser.parse(responseBody);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse currency exchange rates response", e);
            throw new ResponseProcessingException("Failed to parse currency exchange rates response");
        } catch (IOException e) {
            log.error("Failed to fetch currency exchange rates", e);
            throw new ResponseProcessingException("Failed to fetch currency exchange rates");
        }
    }

    private String executeRequest(Request request) throws IOException {
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("HTTP request failed. URL: {}, Code: {}", request.url(), response.code());
                throw new InvalidResponseException("Failed to get currency exchange rate. HTTP Code: " + response.code());
            }

            ResponseBody responseBody = response.body();
            if (responseBody == null || responseBody.contentLength() == 0) {
                log.error("Response body is null. URL: {}", request.url());
                throw new InvalidResponseException("Received empty response body for URL: " + request.url());
            }

            return responseBody.string();
        }
    }
}
