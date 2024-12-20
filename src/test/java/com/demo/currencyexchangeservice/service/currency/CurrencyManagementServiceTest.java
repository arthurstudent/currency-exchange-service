package com.demo.currencyexchangeservice.service.currency;

import com.demo.currencyexchangeservice.base.BaseServiceTest;
import com.demo.currencyexchangeservice.dao.CurrencyRepository;
import com.demo.currencyexchangeservice.domain.CurrencyEntity;
import com.demo.currencyexchangeservice.domain.enums.EnumCurrency;
import com.demo.currencyexchangeservice.dto.AddCurrencyRequestDto;
import com.demo.currencyexchangeservice.service.currency.impl.CurrencyManagementServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class CurrencyManagementServiceTest extends BaseServiceTest {

    @Mock
    private CurrencyRepository currencyRepository;

    @InjectMocks
    private CurrencyManagementServiceImpl currencyManagementService;

    @Test
    void getAllCurrencies_ShouldReturnAllCurrencies() {
        when(currencyRepository.getAllCurrencies())
                .thenReturn(List.of(EnumCurrency.USD, EnumCurrency.EUR));

        List<String> currencies = currencyManagementService.getAllCurrencies();

        assertNotNull(currencies);
        assertEquals(2, currencies.size());
        assertTrue(currencies.contains("USD"));
        assertTrue(currencies.contains("EUR"));
        verify(currencyRepository).getAllCurrencies();
    }

    @Test
    void getAllCurrencies_ShouldReturnEmptyListWhenNoCurrenciesExist() {
        when(currencyRepository.getAllCurrencies()).thenReturn(List.of());

        List<String> currencies = currencyManagementService.getAllCurrencies();

        assertNotNull(currencies);
        assertTrue(currencies.isEmpty());
        verify(currencyRepository).getAllCurrencies();
    }

    @Test
    void addCurrency_ShouldAddNewCurrencies() {
        var requestDto = new AddCurrencyRequestDto();
        requestDto.setCurrencies(List.of(EnumCurrency.GBP, EnumCurrency.EUR));

        when(currencyRepository.existsByCurrency(EnumCurrency.GBP)).thenReturn(false);
        when(currencyRepository.existsByCurrency(EnumCurrency.EUR)).thenReturn(false);

        currencyManagementService.addCurrency(requestDto);

        verify(currencyRepository).saveAll(argThat(entities -> {
            List<CurrencyEntity> entityList = toList(entities);
            return entityList.size() == 2 &&
                    entityList.stream().anyMatch(entity -> entity.getCurrency() == EnumCurrency.GBP) &&
                    entityList.stream().anyMatch(entity -> entity.getCurrency() == EnumCurrency.EUR);
        }));
    }

    @Test
    void addCurrency_ShouldNotAddExistingCurrencies() {
        var requestDto = new AddCurrencyRequestDto();
        requestDto.setCurrencies(List.of(EnumCurrency.USD, EnumCurrency.EUR));

        when(currencyRepository.existsByCurrency(EnumCurrency.USD)).thenReturn(true);
        when(currencyRepository.existsByCurrency(EnumCurrency.EUR)).thenReturn(false);

        currencyManagementService.addCurrency(requestDto);

        verify(currencyRepository).saveAll(argThat(entities -> {
            List<CurrencyEntity> entityList = toList(entities);
            return entityList.size() == 1 &&
                    entityList.stream().anyMatch(entity -> entity.getCurrency() == EnumCurrency.EUR);
        }));

        verify(currencyRepository, never()).saveAll(argThat(entities -> {
            List<CurrencyEntity> entityList = toList(entities);
            return entityList.stream().anyMatch(entity -> entity.getCurrency() == EnumCurrency.USD);
        }));
    }

    @Test
    void addCurrency_ShouldHandleEmptyRequest() {
        var requestDto = new AddCurrencyRequestDto();
        requestDto.setCurrencies(List.of());

        currencyManagementService.addCurrency(requestDto);
        verify(currencyRepository, never()).saveAll(any());
    }

    private List<CurrencyEntity> toList(Iterable<CurrencyEntity> iterable) {
        List<CurrencyEntity> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }
}