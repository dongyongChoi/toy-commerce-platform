package com.toyproject.common.web;

public record ApiResponse<T>(
    boolean success,
    String message,
    T data,
    String errorCode
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, null, data, null);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data, null);
    }

    public static ApiResponse<Void> failure(String message, String errorCode) {
        return new ApiResponse<>(false, message, null, errorCode);
    }
}

