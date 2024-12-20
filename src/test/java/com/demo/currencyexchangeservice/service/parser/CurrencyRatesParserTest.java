package com.demo.currencyexchangeservice.service.parser;

import com.demo.currencyexchangeservice.base.BaseServiceTest;
import com.demo.currencyexchangeservice.domain.enums.EnumCurrency;
import com.demo.currencyexchangeservice.dto.CurrencyRatesDto;
import com.demo.currencyexchangeservice.exceptions.MissedFieldException;
import com.demo.currencyexchangeservice.exceptions.UnknownCurrencyTypeException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyRatesParserTest extends BaseServiceTest {

    private final CurrencyRatesParser parser = new CurrencyRatesParser();

    @Test
    void shouldParseValidJson() throws JsonProcessingException {
        String json = """
                    {
                      "success": true,
                      "source": "USD",
                      "timestamp": 1672531200,
                      "quotes": {
                        "USDEUR": 0.88,
                        "USDGBP": 0.75
                      }
                    }
                """;

        CurrencyRatesDto result = parser.parse(json);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(1672531200), ZoneId.systemDefault());

        assertEquals(EnumCurrency.USD, result.getSourceCurrency());

        assertEquals(localDateTime, result.getTimestamp());
        assertNotNull(result.getRates());
        assertEquals(2, result.getRates().size());
        assertEquals(BigDecimal.valueOf(0.88), result.getRates().get(EnumCurrency.EUR));
        assertEquals(BigDecimal.valueOf(0.75), result.getRates().get(EnumCurrency.GBP));
    }

    @Test
    void shouldHandleEmptyQuotes() {
        String json = """
                    {
                      "success": true,
                      "source": "USD",
                      "timestamp": 1672531200,
                      "quotes": {}
                    }
                """;

        assertThrows(MissedFieldException.class, () -> parser.parse(json));
    }

    @Test
    void shouldHandleUnknownCurrencies() {
        String json = """
                    {
                      "success": true,
                      "source": "USD",
                      "timestamp": 1672531200,
                      "quotes": {
                        "USDXYZ": 1.23
                      }
                    }
                """;

        assertThrows(UnknownCurrencyTypeException.class, () -> parser.parse(json));
    }

    @Test
    void shouldThrowExceptionWhenJsonIsInvalid() {
        String json = "Invalid JSON";

        assertThrows(JsonProcessingException.class, () -> parser.parse(json));
    }

    @Test
    void shouldThrowExceptionWhenQuotesHaveInvalidFormat() {
        String json = """
                    {
                      "success": true,
                      "source": "USD",
                      "timestamp": 1672531200,
                      "quotes": {
                        "USDEUR": "invalid"
                      }
                    }
                """;
        assertThrows(NumberFormatException.class, () -> parser.parse(json));
    }

    @Test
    void shouldHandleZeroRates() throws JsonProcessingException {
        String json = """
                    {
                      "success": true,
                      "source": "USD",
                      "timestamp": 1672531200,
                      "quotes": {
                        "USDEUR": 0.0
                      }
                    }
                """;

        CurrencyRatesDto result = parser.parse(json);

        assertEquals(EnumCurrency.USD, result.getSourceCurrency());
        assertEquals(BigDecimal.valueOf(0.0), result.getRates().get(EnumCurrency.EUR));
    }

    @Test
    void shouldHandleLargeRates() throws JsonProcessingException {
        String json = """
                    {
                      "success": true,
                      "source": "USD",
                      "timestamp": 1672531200,
                      "quotes": {
                        "USDEUR": 999999999.99
                      }
                    }
                """;

        CurrencyRatesDto result = parser.parse(json);

        assertEquals(EnumCurrency.USD, result.getSourceCurrency());
        assertEquals(BigDecimal.valueOf(999999999.99), result.getRates().get(EnumCurrency.EUR));
    }

    @Test
    void shouldHandleMissingFieldsGracefully() {
        String json = """
                    {
                      "success": true,
                      "source": "USD"
                    }
                """;
        assertThrows(MissedFieldException.class, () -> parser.parse(json));
    }

    @Test
    void shouldThrowExceptionForEmptyJson() {
        String json = "{}";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> parser.parse(json));
        assertEquals("Invalid JSON: success is false or null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenQuotesContainNullValue() {
        String json = """
                    {
                      "success": true,
                      "source": "USD",
                      "timestamp": 1672531200,
                      "quotes": {
                        "USDEUR": null
                      }
                    }
                """;

       assertThrows(NumberFormatException.class, () -> parser.parse(json));
    }
}