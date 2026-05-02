package com.toyproject.order.application.port;

public interface StockPort {
    void deduct(Long productId, int quantity);

    void restore(Long productId, int quantity);
}
