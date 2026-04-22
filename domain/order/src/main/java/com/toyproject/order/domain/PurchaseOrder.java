package com.toyproject.order.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

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

    protected PurchaseOrder() {
    }

    public PurchaseOrder(Long memberId, Long productId, int quantity, BigDecimal totalPrice) {
        this.memberId = memberId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.status = "CREATED";
    }

    public Long getId() {
        return id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public Long getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public String getStatus() {
        return status;
    }
}

