package com.toyproject.catalog.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record CreateProductRequest(
    @NotBlank(message = "name must not be blank")
    String name,
    @DecimalMin(value = "0.0", inclusive = false, message = "price must be greater than zero")
    BigDecimal price
) {
}

