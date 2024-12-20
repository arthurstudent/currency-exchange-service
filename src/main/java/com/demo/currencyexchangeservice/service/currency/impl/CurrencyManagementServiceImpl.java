package com.demo.currencyexchangeservice.service.currency.impl;

import com.demo.currencyexchangeservice.dao.CurrencyRepository;
import com.demo.currencyexchangeservice.domain.CurrencyEntity;
import com.demo.currencyexchangeservice.dto.AddCurrencyRequestDto;
import com.demo.currencyexchangeservice.service.currency.CurrencyManagementService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class CurrencyManagementServiceImpl implements CurrencyManagementService {

    private final CurrencyRepository currencyRepository;

    @Override
    public List<String> getAllCurrencies() {
        List<String> currencies = currencyRepository.getAllCurrencies().stream()
                .map(Enum::name)
                .toList();

        log.info("Found {} currencies", currencies.size());
        return currencies;
    }

    @Override
    public void addCurrency(@NonNull AddCurrencyRequestDto addCurrencyRequestDto) {
        List<CurrencyEntity> currencyEntities = addCurrencyRequestDto.getCurrencies().stream()
                .filter(currency -> !currencyRepository.existsByCurrency(currency))
                .map(currency -> new CurrencyEntity().setCurrency(currency))
                .toList();

        if (!currencyEntities.isEmpty()) {
            currencyRepository.saveAll(currencyEntities);
            log.info("Added {} currencies, count - {}", currencyEntities.toString(), currencyEntities.size());
        }
    }
}
