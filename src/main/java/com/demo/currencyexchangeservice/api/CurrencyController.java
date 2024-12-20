package com.demo.currencyexchangeservice.api;

import com.demo.currencyexchangeservice.dto.AddCurrencyRequestDto;
import com.demo.currencyexchangeservice.dto.response.ApiResponse;
import com.demo.currencyexchangeservice.dto.response.CurrenciesResponseDto;
import com.demo.currencyexchangeservice.service.currency.CurrencyManagementService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("v1/currency")
@RestController
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyManagementService currencyManagementService;

    @RateLimiter(name = "api")
    @GetMapping
    public ResponseEntity<?> getAllAddedCurrencies() {
        List<String> allCurrencies = currencyManagementService.getAllCurrencies();
        var currenciesResponseDto = new CurrenciesResponseDto(allCurrencies);
        var currenciesResponseDtoApiResponse = ApiResponse.successResponse(currenciesResponseDto);
        return ResponseEntity.ok().body(currenciesResponseDtoApiResponse);
    }

    @RateLimiter(name = "api")
    @PostMapping
    public ResponseEntity<?> addNewCurrency(@RequestBody AddCurrencyRequestDto addCurrencyRequestDto) {
        currencyManagementService.addCurrency(addCurrencyRequestDto);
        return ResponseEntity.ok().build();
    }
}
