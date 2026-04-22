package com.toyproject.catalog.application;

import com.toyproject.catalog.domain.Product;
import com.toyproject.catalog.domain.ProductRepository;
import com.toyproject.catalog.web.dto.CreateProductRequest;
import com.toyproject.catalog.web.dto.ProductResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductResponse> getProducts() {
        return productRepository.findAll()
            .stream()
            .map(ProductResponse::from)
            .toList();
    }

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = productRepository.save(new Product(request.name(), request.price()));
        return ProductResponse.from(product);
    }
}

