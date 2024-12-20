package com.demo.currencyexchangeservice.dao;

import com.demo.currencyexchangeservice.domain.ExchangeRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeRatesRepository extends JpaRepository<ExchangeRateEntity, Long> {
}
