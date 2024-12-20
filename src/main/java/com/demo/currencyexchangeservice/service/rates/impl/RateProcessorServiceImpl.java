package com.demo.currencyexchangeservice.service.rates.impl;

import com.demo.currencyexchangeservice.dao.CurrencyRepository;
import com.demo.currencyexchangeservice.domain.enums.EnumCurrency;
import com.demo.currencyexchangeservice.dto.CurrencyRatesDto;
import com.demo.currencyexchangeservice.exceptions.DataNotFoundException;
import com.demo.currencyexchangeservice.service.rates.RateFetcherService;
import com.demo.currencyexchangeservice.service.rates.RateProcessorService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class RateProcessorServiceImpl implements RateProcessorService {

    private final RateFetcherService rateFetcherService;

    private final CurrencyRepository currencyRepository;

    @Override
    public CurrencyRatesDto getSpecificRates(@NonNull EnumCurrency baseCurrency, @NonNull Collection<EnumCurrency> targetCurrencies) {
        boolean existsByCurrency = currencyRepository.existsByCurrency(baseCurrency);
        if (!existsByCurrency) {
            log.warn("Base currency - {} is not registered", baseCurrency);
            throw new DataNotFoundException("Base currency is not registered, please add source currency before");
        }

        var ratesForBaseCurrency = rateFetcherService.getAllRatesForBaseCurrency(baseCurrency);

        Map<EnumCurrency, BigDecimal> filteredRates = targetCurrencies.isEmpty() ? ratesForBaseCurrency.getRates() :
                ratesForBaseCurrency.getRates().entrySet()
                .stream()
                .filter(entry -> targetCurrencies.contains(entry.getKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));

        return new CurrencyRatesDto().setSourceCurrency(baseCurrency)
                .setRates(filteredRates)
                .setTimestamp(ratesForBaseCurrency.getTimestamp());
    }
}
