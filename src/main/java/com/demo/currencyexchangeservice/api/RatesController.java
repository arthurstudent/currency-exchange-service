package com.demo.currencyexchangeservice.api;

import com.demo.currencyexchangeservice.commons.CurrencyUtils;
import com.demo.currencyexchangeservice.domain.enums.EnumCurrency;
import com.demo.currencyexchangeservice.dto.response.ApiResponse;
import com.demo.currencyexchangeservice.service.rates.RateProcessorService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("v1/exchange_rates")
@RestController
@RequiredArgsConstructor
public class RatesController {

    private final RateProcessorService rateProcessorService;

       /*
    @RequestParam(name = "targets") List<EnumCurrency> was replaced with List<String> just to make openapi docs more user-friendly
    * */

    @Operation(
            summary = "Retrieve exchange rates",
            description = "Fetches exchange rates for a source currency to one or more target currencies.",
            tags = { "Exchange Rate Management" }
    )
    @RateLimiter(name = "api")
    @GetMapping
    public ResponseEntity<?> getRates(
            @Parameter(description = "Source currency for the exchange rates", example = "USD")
            @RequestParam(name = "source") EnumCurrency currency,

            @Parameter(description = "List of target currencies", example = "USD,EUR,GBP")
            @RequestParam(name = "targets") List<String> targets) {
        List<EnumCurrency> currencyList = targets.stream()
                .map(CurrencyUtils::wrapEnumCurrency)
                .toList();
        var specificRates = rateProcessorService.getSpecificRates(currency, currencyList);
        var currencyRatesDtoApiResponse = ApiResponse.successResponse(specificRates);
        return ResponseEntity.ok(currencyRatesDtoApiResponse);
    }
}
