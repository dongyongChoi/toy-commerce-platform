package com.toyproject.catalog.application;

import com.toyproject.catalog.domain.Product;
import com.toyproject.catalog.domain.ProductRepository;
import com.toyproject.catalog.web.dto.CreateProductRequest;
import com.toyproject.catalog.web.dto.ProductResponse;
import com.toyproject.catalog.web.dto.UpdateProductRequest;
import com.toyproject.common.core.DomainException;
import com.toyproject.common.core.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;

    @Cacheable(cacheNames = "products", key = "'all'")
    public List<ProductResponse> getProducts() {
        return productRepository.findAll()
            .stream()
            .map(ProductResponse::from)
            .toList();
    }

    @Cacheable(cacheNames = "products", key = "#productId")
    public ProductResponse getProduct(Long productId) {
        return ProductResponse.from(findProduct(productId));
    }

    @Transactional
    @CacheEvict(cacheNames = "products", allEntries = true)
    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = productRepository.save(new Product(request.name(), request.price()));
        return ProductResponse.from(product);
    }

    @Transactional
    @CacheEvict(cacheNames = "products", allEntries = true)
    public ProductResponse updateProduct(Long productId, UpdateProductRequest request) {
        Product product = findProduct(productId);
        product.update(request.name(), request.price());
        return ProductResponse.from(product);
    }

    @Transactional
    @CacheEvict(cacheNames = "products", allEntries = true)
    public void deleteProduct(Long productId) {
        Product product = findProduct(productId);
        productRepository.delete(product);
    }

    private Product findProduct(Long productId) {
        return productRepository.findById(productId)
            .orElseThrow(() -> new DomainException(ErrorCode.RESOURCE_NOT_FOUND, "product not found"));
    }
}
