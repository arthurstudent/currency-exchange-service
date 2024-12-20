package com.demo.currencyexchangeservice.service.parser;

import com.demo.currencyexchangeservice.commons.CurrencyUtils;
import com.demo.currencyexchangeservice.domain.enums.EnumCurrency;
import com.demo.currencyexchangeservice.dto.CurrencyRatesDto;
import com.demo.currencyexchangeservice.exceptions.MissedFieldException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Log4j2
@Component
@NoArgsConstructor
public class CurrencyRatesParser {

    public CurrencyRatesDto parse(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(json);

        JsonNode successNode = rootNode.get("success");
        if (successNode == null || !successNode.asBoolean()) {
            log.warn("Unable to parse currency rates from json");
            throw new IllegalArgumentException("Invalid JSON: success is false or null");
        }

        String source = getSource(rootNode);
        LocalDateTime timestampDateTime = getDateTime(rootNode);

        JsonNode quotesNode = getQuotes(rootNode);

        if (!quotesNode.fields().hasNext()) {
            log.warn("Unable to parse quotes from json, list is empty");
            throw new MissedFieldException("Quotes list is empty");
        }

        Map<EnumCurrency, BigDecimal> rates = StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(quotesNode.fields(), Spliterator.ORDERED), false)
                .collect(Collectors.toMap(
                        entry -> CurrencyUtils.wrapEnumCurrency(entry.getKey().replaceFirst(source, "")),
                        entry -> new BigDecimal(entry.getValue().asText()),
                        (existing, replacement) -> replacement,
                        () -> new EnumMap<>(EnumCurrency.class)
                ));

        EnumCurrency sourceCurrency = CurrencyUtils.wrapEnumCurrency(source);

        return new CurrencyRatesDto().setRates(rates)
                .setSourceCurrency(sourceCurrency)
                .setTimestamp(timestampDateTime);
    }

    private JsonNode getQuotes(JsonNode rootNode) {
        String quotes = "quotes";
        return getJsonNodeValue(rootNode, quotes, (jsonNode -> jsonNode));
    }

    private LocalDateTime getDateTime(JsonNode rootNode) {
        return getJsonNodeValue(rootNode, "timestamp", node ->
                LocalDateTime.ofInstant(Instant.ofEpochSecond(node.asLong()), ZoneId.systemDefault()));
    }

    private String getSource(JsonNode rootNode) {
        return getJsonNodeValue(rootNode, "source", JsonNode::asText);
    }

    private <T> T getJsonNodeValue(JsonNode rootNode, String fieldName, Function<JsonNode, T> extractor) {
        JsonNode fieldNode = rootNode.get(fieldName);
        if (fieldNode == null) {
            log.warn("Invalid JSON: {} is absent", fieldName);
            throw new MissedFieldException("Invalid JSON: " + fieldName + " is absent");
        }
        return extractor.apply(fieldNode);
    }
}
