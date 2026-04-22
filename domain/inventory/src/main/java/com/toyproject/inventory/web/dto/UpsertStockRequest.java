package com.toyproject.inventory.web.dto;

import jakarta.validation.constraints.Min;

public record UpsertStockRequest(
    @Min(value = 0, message = "quantity must be zero or greater")
    int quantity
) {
}

