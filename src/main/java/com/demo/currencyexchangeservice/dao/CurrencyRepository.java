package com.demo.currencyexchangeservice.dao;

import com.demo.currencyexchangeservice.domain.CurrencyEntity;
import com.demo.currencyexchangeservice.domain.enums.EnumCurrency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurrencyRepository extends JpaRepository<CurrencyEntity, Long> {

    @Query("select c.currency from CurrencyEntity c")
    List<EnumCurrency> getAllCurrencies();

    boolean existsByCurrency(EnumCurrency currency);
}
