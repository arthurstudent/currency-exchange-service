package com.demo.currencyexchangeservice.dto;

import com.demo.currencyexchangeservice.domain.enums.EnumCurrency;
import lombok.Data;

import java.util.List;

@Data
public class GetCurrencyExchangeDto {
    private final EnumCurrency baseCurrency;
}