package com.toyproject.order.application.event;

import com.toyproject.order.domain.PurchaseOrder;

import java.math.BigDecimal;

public record OrderConfirmedEvent(
    Long orderId,
    Long memberId,
    Long productId,
    int quantity,
    BigDecimal totalPrice
) {
    public static OrderConfirmedEvent from(PurchaseOrder order) {
        return new OrderConfirmedEvent(
            order.getId(),
            order.getMemberId(),
            order.getProductId(),
            order.getQuantity(),
            order.getTotalPrice()
        );
    }
}
