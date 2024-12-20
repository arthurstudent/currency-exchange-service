package com.demo.currencyexchangeservice.dto;

import com.demo.currencyexchangeservice.domain.enums.EnumCurrency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyDto {
    private EnumCurrency currency;
}
