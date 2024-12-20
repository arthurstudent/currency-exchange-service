package com.demo.currencyexchangeservice.config.configurer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@NoArgsConstructor
public class ExchangeApiConfigurer {

    @Value("${exchange-api.api-url}")
    private String apiUrl;

    @Value("${exchange-api.api-key}")
    private String apiKey;
}
