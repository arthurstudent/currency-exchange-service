package com.demo.currencyexchangeservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CurrenciesResponseDto {
    private List<String> currencies;
}
