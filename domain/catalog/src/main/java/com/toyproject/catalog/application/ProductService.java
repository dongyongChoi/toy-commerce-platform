package com.toyproject.catalog.application;

import com.toyproject.catalog.domain.Product;
import com.toyproject.catalog.domain.ProductRepository;
import com.toyproject.catalog.web.dto.CreateProductRequest;
import com.toyproject.catalog.web.dto.ProductResponse;
import com.toyproject.catalog.web.dto.UpdateProductRequest;
import com.toyproject.common.core.DomainException;
import com.toyproject.common.core.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public ProductResponse getProduct(Long productId) {
        return ProductResponse.from(findProduct(productId));
    }

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = productRepository.save(new Product(request.name(), request.price()));
        return ProductResponse.from(product);
    }

    @Transactional
    public ProductResponse updateProduct(Long productId, UpdateProductRequest request) {
        Product product = findProduct(productId);
        product.update(request.name(), request.price());
        return ProductResponse.from(product);
    }

    @Transactional
    public void deleteProduct(Long productId) {
        Product product = findProduct(productId);
        productRepository.delete(product);
    }

    private Product findProduct(Long productId) {
        return productRepository.findById(productId)
            .orElseThrow(() -> new DomainException(ErrorCode.RESOURCE_NOT_FOUND, "product not found"));
    }
}

