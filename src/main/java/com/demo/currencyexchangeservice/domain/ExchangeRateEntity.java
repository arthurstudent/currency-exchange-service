package com.demo.currencyexchangeservice.domain;

import com.demo.currencyexchangeservice.domain.enums.EnumCurrency;
import jakarta.persistence.*;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Entity
@EqualsAndHashCode(callSuper = true)
@ToString
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@Table(name = "exchange_rates")
public class ExchangeRateEntity extends BaseEntity {

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnumCurrency targetCurrency;

    @ManyToOne
    @JoinColumn(name = "currency_id", nullable = false)
    private CurrencyEntity currencyEntity;
}
