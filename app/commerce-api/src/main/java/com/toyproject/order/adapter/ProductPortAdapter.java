package com.toyproject.order.adapter;

import com.toyproject.catalog.domain.ProductRepository;
import com.toyproject.order.application.port.ProductPort;
import com.toyproject.order.application.port.dto.ProductSnapshot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class ProductPortAdapter implements ProductPort {
    private final ProductRepository productRepository;

    @Override
    public Optional<ProductSnapshot> findById(Long productId) {
        return productRepository.findById(productId)
            .map(product -> new ProductSnapshot(product.getId(), product.getPrice()));
    }
}
