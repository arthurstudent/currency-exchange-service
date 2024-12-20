package com.demo.currencyexchangeservice.exceptions.handler;

import com.demo.currencyexchangeservice.dto.response.ApiResponse;
import com.demo.currencyexchangeservice.exceptions.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@ControllerAdvice
public class DefaultExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .filter(e -> e.getDefaultMessage() != null)
                .collect(Collectors.toMap(FieldError::getField,
                        DefaultMessageSourceResolvable::getDefaultMessage
                ));

        ApiResponse<Map<String, String>> mapApiResponse = ApiResponse.failResponse(errors);
        return ResponseEntity.badRequest().body(mapApiResponse);
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleDataNotFoundException(DataNotFoundException ex) {
        ApiResponse<String> apiResponse = ApiResponse.failResponse(ex.getMessage());
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidResponseException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidResponseException(InvalidResponseException ex) {
        ApiResponse<String> apiResponse = ApiResponse.failResponse(ex.getMessage());
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UnknownCurrencyTypeException.class)
    public ResponseEntity<ApiResponse<String>> handleUnknownCurrencyTypeException(UnknownCurrencyTypeException ex) {
        ApiResponse<String> apiResponse = ApiResponse.failResponse(ex.getMessage());
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MissedFieldException.class)
    public ResponseEntity<ApiResponse<String>> handleMissedFieldException(MissedFieldException ex) {
        ApiResponse<String> apiResponse = ApiResponse.failResponse(ex.getMessage());
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> handleBaseException(RuntimeException ex) {
        log.error("Exception", ex);
        ApiResponse<String> apiResponse = ApiResponse.failResponse("Something went wrong, unexpected exception occurred");
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResponseProcessingException.class)
    public ResponseEntity<ApiResponse<String>> handleResponseProcessingException(ResponseProcessingException ex) {
        log.error("HttpMessageNotReadableException", ex);
        ApiResponse<String> apiResponse = ApiResponse.failResponse("Unable to parse request body, please check passed arguments");
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error("Data integrity violation error: ", ex);
        String userFriendlyMessage = "Data already exists or unique restrictions are violated";
        ApiResponse<String> apiResponse = ApiResponse.failResponse(userFriendlyMessage);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }
}
