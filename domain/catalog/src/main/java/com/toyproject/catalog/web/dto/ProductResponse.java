package com.toyproject.catalog.web.dto;

import com.toyproject.catalog.domain.Product;
import java.io.Serializable;
import java.math.BigDecimal;

public record ProductResponse(
    Long id,
    String name,
    BigDecimal price
) implements Serializable {
    private static final long serialVersionUID = 1L;

    public static ProductResponse from(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getPrice());
    }
}
