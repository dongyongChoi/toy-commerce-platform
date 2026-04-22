package com.toyproject.order.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

public record CreateOrderRequest(
    @Min(value = 1, message = "memberId must be greater than zero")
    Long memberId,
    @Min(value = 1, message = "productId must be greater than zero")
    Long productId,
    @Min(value = 1, message = "quantity must be greater than zero")
    int quantity,
    @DecimalMin(value = "0.0", inclusive = false, message = "totalPrice must be greater than zero")
    BigDecimal totalPrice
) {
}

