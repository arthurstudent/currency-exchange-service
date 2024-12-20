package com.demo.currencyexchangeservice.dto;

import com.demo.currencyexchangeservice.domain.enums.EnumCurrency;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class CurrencyRatesDto {
    private Map<EnumCurrency, BigDecimal> rates;
    private LocalDateTime timestamp;
    private EnumCurrency sourceCurrency;
}