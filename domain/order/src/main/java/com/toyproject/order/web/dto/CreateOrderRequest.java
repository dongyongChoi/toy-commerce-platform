package com.toyproject.order.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
    @NotNull(message = "memberId must not be null")
    @Min(value = 1, message = "memberId must be greater than zero")
    Long memberId,
    @NotNull(message = "productId must not be null")
    @Min(value = 1, message = "productId must be greater than zero")
    Long productId,
    @Min(value = 1, message = "quantity must be greater than zero")
    int quantity
) {
}

