package com.toyproject.settlement.application.port.dto;

import com.toyproject.order.application.event.OrderConfirmedEvent;

import java.math.BigDecimal;

public record LegacySettlementRequest(
    Long orderId,
    Long memberId,
    Long productId,
    int quantity,
    BigDecimal totalPrice
) {
    public static LegacySettlementRequest from(OrderConfirmedEvent event) {
        return new LegacySettlementRequest(
            event.orderId(),
            event.memberId(),
            event.productId(),
            event.quantity(),
            event.totalPrice()
        );
    }
}
