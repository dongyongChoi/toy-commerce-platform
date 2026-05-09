package com.toyproject.inventory.domain;

import com.toyproject.common.core.DomainException;
import com.toyproject.common.core.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "stock_items")
public class StockItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long productId;

    @Column(nullable = false)
    private int quantity;

    public StockItem(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void deduct(int quantity) {
        if (this.quantity < quantity) {
            throw new DomainException(ErrorCode.INVALID_INPUT, "재고가 부족합니다.");
        }
        this.quantity -= quantity;
    }

    public void restore(int quantity) {
        this.quantity += quantity;
    }
}
