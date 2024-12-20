package com.demo.currencyexchangeservice.api;

import com.demo.currencyexchangeservice.dto.AddCurrencyRequestDto;
import com.demo.currencyexchangeservice.dto.response.ApiResponse;
import com.demo.currencyexchangeservice.dto.response.CurrenciesResponseDto;
import com.demo.currencyexchangeservice.service.currency.CurrencyManagementService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("v1/currency")
@RestController
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyManagementService currencyManagementService;

    @Operation(
            summary = "Retrieve all added currencies",
            description = "Fetches a list of all currencies currently tracked in the system.",
            tags = {"Currency Management"}
    )
    @RateLimiter(name = "api")
    @GetMapping
    public ResponseEntity<?> getAllAddedCurrencies() {
        List<String> allCurrencies = currencyManagementService.getAllCurrencies();
        var currenciesResponseDto = new CurrenciesResponseDto(allCurrencies);
        var currenciesResponseDtoApiResponse = ApiResponse.successResponse(currenciesResponseDto);
        return ResponseEntity.ok().body(currenciesResponseDtoApiResponse);
    }

    @Operation(
            summary = "Add a new currency",
            description = "Adds a new currency to be tracked in the system.",
            tags = {"Currency Management"}
    )
    @RequestBody(
            description = "Request body containing the currency to be added",
            required = true,
            content = @Content(schema = @Schema(implementation = AddCurrencyRequestDto.class))
    )
    @RateLimiter(name = "api")
    @PostMapping
    public ResponseEntity<?> addNewCurrency(@org.springframework.web.bind.annotation.RequestBody AddCurrencyRequestDto addCurrencyRequestDto) {
        currencyManagementService.addCurrency(addCurrencyRequestDto);
        return ResponseEntity.ok().build();
    }
}
