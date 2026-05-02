package com.toyproject.order.adapter;

import com.toyproject.catalog.domain.ProductRepository;
import com.toyproject.order.application.port.ProductPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProductPortAdapter implements ProductPort {
    private final ProductRepository productRepository;

    @Override
    public boolean exists(Long productId) {
        return productRepository.existsById(productId);
    }
}
