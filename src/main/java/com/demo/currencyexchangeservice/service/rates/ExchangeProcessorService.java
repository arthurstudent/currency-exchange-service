package com.demo.currencyexchangeservice.service.rates;

import com.demo.currencyexchangeservice.domain.enums.EnumCurrency;
import com.demo.currencyexchangeservice.dto.response.ExchangeResponseDto;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.Collection;

public interface ExchangeProcessorService {
    ExchangeResponseDto calculateExchangeRate(@NonNull BigDecimal amount, @NonNull EnumCurrency base, @NonNull Collection<EnumCurrency> targets);
}
