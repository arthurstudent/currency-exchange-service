package com.demo.currencyexchangeservice.dto;

import com.demo.currencyexchangeservice.domain.enums.EnumCurrency;

import java.math.BigDecimal;

public record RateInfoDto(EnumCurrency currency, BigDecimal rate) {}
