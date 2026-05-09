package com.toyproject.order.application.port.dto;

import java.math.BigDecimal;

public record ProductSnapshot(
    Long id,
    BigDecimal price
) {
}
