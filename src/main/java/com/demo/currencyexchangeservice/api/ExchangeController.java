package com.demo.currencyexchangeservice.api;

import com.demo.currencyexchangeservice.commons.CurrencyUtils;
import com.demo.currencyexchangeservice.domain.enums.EnumCurrency;
import com.demo.currencyexchangeservice.dto.response.ApiResponse;
import com.demo.currencyexchangeservice.service.rates.ExchangeProcessorService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RequestMapping("v1/exchange")
@RestController
@RequiredArgsConstructor
public class ExchangeController {

    private final ExchangeProcessorService exchangeProcessorService;

    /*
    @RequestParam(name = "targets") List<EnumCurrency> was replaced with List<String> just to make openapi docs more user-friendly
    * */

    @RateLimiter(name = "api")
    @GetMapping
    public ResponseEntity<?> convertCurrency(@RequestParam(name = "source") EnumCurrency from,
                                             @Parameter(description = "List of target currencies", example = "USD,EUR,GBP")
                                             @RequestParam(name = "targets") List<String> to,
                                             @RequestParam(name = "amount") BigDecimal amount) {
        List<EnumCurrency> currencyList = to.stream()
                .map(CurrencyUtils::wrapEnumCurrency)
                .toList();
        var exchangeResponseDto = exchangeProcessorService.calculateExchangeRate(amount, from, currencyList);
        var exchangeResponseDtoApiResponse = ApiResponse.successResponse(exchangeResponseDto);
        return ResponseEntity.ok(exchangeResponseDtoApiResponse);
    }
}
