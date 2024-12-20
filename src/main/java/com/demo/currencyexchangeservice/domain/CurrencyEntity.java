package com.demo.currencyexchangeservice.domain;

import com.demo.currencyexchangeservice.domain.enums.EnumCurrency;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

@Entity
@Table(name = "currencies")
@Accessors(chain = true)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CurrencyEntity extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private EnumCurrency currency;
}
