package com.demo.currencyexchangeservice.service.rates.impl;

import com.demo.currencyexchangeservice.domain.enums.EnumCurrency;
import com.demo.currencyexchangeservice.dto.CurrencyRatesDto;
import com.demo.currencyexchangeservice.dto.RateInfoDto;
import com.demo.currencyexchangeservice.dto.response.ExchangeResponseDto;
import com.demo.currencyexchangeservice.service.rates.ExchangeProcessorService;
import com.demo.currencyexchangeservice.service.rates.RateProcessorService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class ExchangeProcessorServiceImpl implements ExchangeProcessorService {

    private final RateProcessorService rateProcessorService;

    @Override
    public ExchangeResponseDto calculateExchangeRate(@NonNull BigDecimal amount, @NonNull EnumCurrency base, @NonNull Collection<EnumCurrency> targets) {
        log.info("Calculate exchange rate is called, base currency - {}, base amount - {}", base, amount);

        CurrencyRatesDto specificRates = rateProcessorService.getSpecificRates(base, targets);

        Map<EnumCurrency, BigDecimal> exchanges = specificRates.getRates().entrySet()
                .stream()
                .map(entry -> calculateExchange(entry.getKey(), entry.getValue(), amount))
                .collect(Collectors.toMap(RateInfoDto::currency, RateInfoDto::rate));

        var exchangeResponseDto = new ExchangeResponseDto()
                .setAmount(amount)
                .setRates(exchanges)
                .setBase(base)
                .setDateTime(LocalDateTime.now());

        log.info("Exchanges calculated successfully");
        return exchangeResponseDto;
    }

    private RateInfoDto calculateExchange(@NonNull EnumCurrency calculateCurrency, @NonNull BigDecimal calculateRate, @NonNull BigDecimal baseAmount) {
        double calculatedAmount = calculateRate.multiply(baseAmount, new MathContext(5, RoundingMode.CEILING)).doubleValue();
        return new RateInfoDto(calculateCurrency, BigDecimal.valueOf(calculatedAmount));
    }
}
