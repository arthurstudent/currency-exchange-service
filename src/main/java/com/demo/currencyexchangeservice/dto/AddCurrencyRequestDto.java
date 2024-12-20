package com.demo.currencyexchangeservice.dto;

import com.demo.currencyexchangeservice.domain.enums.EnumCurrency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddCurrencyRequestDto {
    private List<EnumCurrency> currencies;
}
