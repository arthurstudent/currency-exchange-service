package com.demo.currencyexchangeservice.service.rates.scheduled;

import com.demo.currencyexchangeservice.dao.CurrencyRepository;
import com.demo.currencyexchangeservice.dao.ExchangeRatesRepository;
import com.demo.currencyexchangeservice.domain.CurrencyEntity;
import com.demo.currencyexchangeservice.domain.ExchangeRateEntity;
import com.demo.currencyexchangeservice.dto.CurrencyRatesDto;
import com.demo.currencyexchangeservice.service.rates.RateFetcherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class CurrencyRatesUpdater {

    private final CurrencyRepository currencyRepository;
    private final ExchangeRatesRepository exchangeRatesRepository;
    private final RateFetcherService rateFetcherService;

    @Scheduled(cron = "0 * * * * ?")
    public void updateCurrencyRates() {
        rateFetcherService.clearCache();
        log.info("Starting scheduled currency rates update");

        List<CurrencyEntity> baseCurrencies = currencyRepository.findAll();
        if (baseCurrencies.isEmpty()) {
            log.warn("No base currencies found in the database. Skipping update.");
            return;
        }

        List<ExchangeRateEntity> exchangeRateEntities = new ArrayList<>();

        baseCurrencies.forEach(baseCurrency -> {
                CurrencyRatesDto ratesForBaseCurrency = rateFetcherService.getAllRatesForBaseCurrency(baseCurrency.getCurrency());

                ratesForBaseCurrency.getRates().forEach((targetCurrency, rate) -> {
                    ExchangeRateEntity exchangeRateEntity = new ExchangeRateEntity()
                            .setCurrencyEntity(baseCurrency)
                            .setTargetCurrency(targetCurrency)
                            .setAmount(rate);
                    exchangeRateEntities.add(exchangeRateEntity);
                });
        });

        if (!exchangeRateEntities.isEmpty()) {
            exchangeRatesRepository.saveAll(exchangeRateEntities);
            log.info("Saved {} exchange rates to the database.", exchangeRateEntities.size());
        } else {
            log.info("No exchange rates to save.");
        }
    }
}
