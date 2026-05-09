package com.toyproject.order.application.port;

import com.toyproject.order.application.port.dto.ProductSnapshot;

import java.util.Optional;

public interface ProductPort {
    Optional<ProductSnapshot> findById(Long productId);
}
