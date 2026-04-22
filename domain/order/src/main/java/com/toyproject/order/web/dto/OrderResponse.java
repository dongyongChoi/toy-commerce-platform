package com.toyproject.order.web.dto;

import com.toyproject.order.domain.PurchaseOrder;
import java.math.BigDecimal;

public record OrderResponse(
    Long id,
    Long memberId,
    Long productId,
    int quantity,
    BigDecimal totalPrice,
    String status
) {
    public static OrderResponse from(PurchaseOrder order) {
        return new OrderResponse(
            order.getId(),
            order.getMemberId(),
            order.getProductId(),
            order.getQuantity(),
            order.getTotalPrice(),
            order.getStatus()
        );
    }
}

