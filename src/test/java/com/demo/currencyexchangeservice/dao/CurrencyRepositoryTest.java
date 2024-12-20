package com.demo.currencyexchangeservice.dao;

import com.demo.currencyexchangeservice.base.JpaRepositoryTest;
import com.demo.currencyexchangeservice.domain.CurrencyEntity;
import com.demo.currencyexchangeservice.domain.enums.EnumCurrency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CurrencyRepositoryTest extends JpaRepositoryTest {

    @Autowired
    private CurrencyRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void testGetAllCurrencies() {
        createTestCurrencies();
        List<EnumCurrency> currencyList = repository.getAllCurrencies();

        assertThat(currencyList)
                .isNotEmpty()
                .hasSize(2)
                .containsExactlyInAnyOrder(EnumCurrency.USD, EnumCurrency.EUR);
    }

    @ParameterizedTest
    @ValueSource(strings = {"USD", "EUR"})
    void testExistsByCurrency(String currency) {
        createTestCurrencies();
        boolean exists = repository.existsByCurrency(EnumCurrency.valueOf(currency));
        assertTrue(exists);
    }

    @Test
    void testCurrencyDuplicateInsert() {
        var currencyEntity = new CurrencyEntity().setCurrency(EnumCurrency.USD);
        var currencyEntity2 = new CurrencyEntity().setCurrency(EnumCurrency.USD);

        repository.save(currencyEntity);
        assertThrows(DataIntegrityViolationException.class, () -> repository.save(currencyEntity2));
    }

    private void createTestCurrencies() {
        List<CurrencyEntity> currencies = Arrays.asList(
                new CurrencyEntity().setCurrency(EnumCurrency.USD),
                new CurrencyEntity().setCurrency(EnumCurrency.EUR)
        );
        repository.saveAll(currencies);
    }
}