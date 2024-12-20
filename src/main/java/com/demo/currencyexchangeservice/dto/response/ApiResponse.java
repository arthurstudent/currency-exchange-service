package com.demo.currencyexchangeservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static java.lang.Math.ceil;
import static java.math.BigDecimal.valueOf;

@Data
public class ApiResponse<T> {

    @JsonInclude(NON_NULL)
    private T data;
    private boolean success;
    @JsonInclude(NON_NULL)
    private PageData pageData;

    @Data
    private static class PageData {
        private Long totalItems;
        private Integer totalPages;
        private Integer page;
        private Integer size;
    }

    public static ApiResponse<?> successResponse() {
        var response = new ApiResponse<>();
        response.setSuccess(true);
        return response;
    }

    public static <T> ApiResponse<T> successResponse(T data, Long totalItems, Integer page, Integer size) {
        var response = successResponse(data);

        var pageData = new PageData();
        pageData.setTotalItems(totalItems);
        pageData.setPage(page);
        pageData.setSize(size);
        pageData.setTotalPages(valueOf(ceil((double) totalItems / size)).intValue());

        response.setPageData(pageData);

        return response;
    }

    public static <T> ApiResponse<T> successResponse(T data) {
        var response = new ApiResponse<T>();
        response.setData(data);
        response.setSuccess(true);
        return response;
    }

    public static <T> ApiResponse<T> failResponse(T data) {
        var response = new ApiResponse<T>();
        response.setData(data);
        response.setSuccess(false);
        return response;
    }
}