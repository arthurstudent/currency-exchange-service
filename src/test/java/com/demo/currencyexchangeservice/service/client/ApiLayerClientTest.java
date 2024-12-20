package com.demo.currencyexchangeservice.service.client;

import com.demo.currencyexchangeservice.base.BaseIntegrationTest;
import com.demo.currencyexchangeservice.config.configurer.ExchangeApiConfigurer;
import com.demo.currencyexchangeservice.domain.enums.EnumCurrency;
import com.demo.currencyexchangeservice.dto.CurrencyRatesDto;
import com.demo.currencyexchangeservice.dto.GetCurrencyExchangeDto;
import com.demo.currencyexchangeservice.exceptions.InvalidResponseException;
import com.demo.currencyexchangeservice.service.client.impl.ApiLayerClientImpl;
import com.demo.currencyexchangeservice.service.parser.CurrencyRatesParser;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

class ApiLayerClientTest extends BaseIntegrationTest {

    @Autowired
    private ApiLayerClientImpl apiLayerClient;

    @MockBean
    private ExchangeApiConfigurer exchangeApiConfigurer;

    @MockBean
    private CurrencyRatesParser currencyRatesParser;

    private MockWebServer mockWebServer;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        when(exchangeApiConfigurer.getApiUrl()).thenReturn(mockWebServer.url("/").toString());
        when(exchangeApiConfigurer.getApiKey()).thenReturn("test-api-key");
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testGetCurrencyExchangeRate_successfulResponse() throws Exception {
        String mockResponse = "{ \"success\": true, \"quotes\": { \"USDGBP\": 0.72, \"USDEUR\": 0.84 } }";
        mockWebServer.enqueue(new MockResponse()
                .setBody(mockResponse)
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json"));

        CurrencyRatesDto mockCurrencyRatesDto = new CurrencyRatesDto();
        when(currencyRatesParser.parse(mockResponse)).thenReturn(mockCurrencyRatesDto);

        GetCurrencyExchangeDto requestDto = new GetCurrencyExchangeDto(EnumCurrency.USD);
        CurrencyRatesDto responseDto = apiLayerClient.getCurrencyExchangeRate(requestDto);

        assertThat(responseDto).isNotNull();
        assertThat(responseDto).isEqualTo(mockCurrencyRatesDto);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertThat(recordedRequest.getPath()).contains("/live?source=USD");
        assertThat(recordedRequest.getHeader("apikey")).isEqualTo("test-api-key");
    }

    @Test
    void testGetCurrencyExchangeRate_errorResponse() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("{\"error\": \"Internal Server Error\"}"));

        GetCurrencyExchangeDto requestDto = new GetCurrencyExchangeDto(EnumCurrency.USD);

        assertThatThrownBy(() -> apiLayerClient.getCurrencyExchangeRate(requestDto))
                .isInstanceOf(InvalidResponseException.class)
                .hasMessageContaining("Failed to get currency exchange rate");

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertThat(recordedRequest.getPath()).contains("/live?source=USD");
    }

    @Test
    void testGetCurrencyExchangeRate_emptyResponseBody() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200));

        GetCurrencyExchangeDto requestDto = new GetCurrencyExchangeDto(EnumCurrency.USD);

        assertThatThrownBy(() -> apiLayerClient.getCurrencyExchangeRate(requestDto))
                .isInstanceOf(InvalidResponseException.class)
                .hasMessageContaining("Received empty response body");
    }
}