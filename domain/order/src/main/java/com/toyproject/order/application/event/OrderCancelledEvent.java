package com.toyproject.order.application.event;

import com.toyproject.order.domain.PurchaseOrder;

import java.math.BigDecimal;

public record OrderCancelledEvent(
    Long orderId,
    Long memberId,
    Long productId,
    int quantity,
    BigDecimal totalPrice
) {
    public static OrderCancelledEvent from(PurchaseOrder order) {
        return new OrderCancelledEvent(
            order.getId(),
            order.getMemberId(),
            order.getProductId(),
            order.getQuantity(),
            order.getTotalPrice()
        );
    }
}
