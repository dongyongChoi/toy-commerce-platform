package com.toyproject.order.application.event;

import com.toyproject.order.domain.PurchaseOrder;

import java.math.BigDecimal;

public record OrderCreatedEvent(
    Long orderId,
    Long memberId,
    Long productId,
    int quantity,
    BigDecimal totalPrice
) {
    public static OrderCreatedEvent from(PurchaseOrder order) {
        return new OrderCreatedEvent(
            order.getId(),
            order.getMemberId(),
            order.getProductId(),
            order.getQuantity(),
            order.getTotalPrice()
        );
    }
}
