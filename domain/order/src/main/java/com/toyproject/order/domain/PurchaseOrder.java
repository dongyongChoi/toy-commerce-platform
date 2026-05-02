package com.toyproject.order.domain;

import com.toyproject.common.core.DomainException;
import com.toyproject.common.core.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "purchase_orders")
public class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalPrice;

    @Column(nullable = false)
    private String status;

    public PurchaseOrder(Long memberId, Long productId, int quantity, BigDecimal totalPrice) {
        this.memberId = memberId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.status = "CREATED";
    }

    public void confirm() {
        if (!"CREATED".equals(this.status)) {
            throw new DomainException(ErrorCode.INVALID_INPUT, "주문 확정은 CREATED 상태에서만 가능합니다.");
        }
        this.status = "CONFIRMED";
    }

    public void cancel() {
        if ("CANCELLED".equals(this.status)) {
            throw new DomainException(ErrorCode.INVALID_INPUT, "이미 취소된 주문입니다.");
        }
        this.status = "CANCELLED";
    }
}
