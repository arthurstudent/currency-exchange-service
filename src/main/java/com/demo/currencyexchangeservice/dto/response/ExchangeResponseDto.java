package com.demo.currencyexchangeservice.dto.response;

import com.demo.currencyexchangeservice.domain.enums.EnumCurrency;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Accessors(chain = true)
@Data
@NoArgsConstructor
public class ExchangeResponseDto {
    private EnumCurrency base;
    private BigDecimal amount;
    private LocalDateTime dateTime;
    private Map<EnumCurrency, BigDecimal> rates;
}
