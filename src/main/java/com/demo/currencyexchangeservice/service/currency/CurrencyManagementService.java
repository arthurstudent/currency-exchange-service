package com.demo.currencyexchangeservice.service.currency;

import com.demo.currencyexchangeservice.dto.AddCurrencyRequestDto;
import lombok.NonNull;

import java.util.List;

public interface CurrencyManagementService {
    List<String> getAllCurrencies();

    void addCurrency(@NonNull AddCurrencyRequestDto addCurrencyRequestDto);
}
