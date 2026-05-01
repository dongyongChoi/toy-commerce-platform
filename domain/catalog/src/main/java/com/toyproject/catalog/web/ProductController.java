package com.toyproject.catalog.web;

import com.toyproject.catalog.application.ProductService;
import com.toyproject.catalog.web.dto.CreateProductRequest;
import com.toyproject.catalog.web.dto.ProductResponse;
import com.toyproject.catalog.web.dto.UpdateProductRequest;
import com.toyproject.common.web.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ApiResponse<List<ProductResponse>> getProducts() {
        return ApiResponse.success(productService.getProducts());
    }

    @GetMapping("/{productId}")
    public ApiResponse<ProductResponse> getProduct(@PathVariable("productId") Long productId) {
        return ApiResponse.success(productService.getProduct(productId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        return ApiResponse.success(productService.createProduct(request), "product created");
    }

    @PutMapping("/{productId}")
    public ApiResponse<ProductResponse> updateProduct(
        @PathVariable("productId") Long productId,
        @Valid @RequestBody UpdateProductRequest request
    ) {
        return ApiResponse.success(productService.updateProduct(productId, request), "product updated");
    }

    @DeleteMapping("/{productId}")
    public ApiResponse<Void> deleteProduct(@PathVariable("productId") Long productId) {
        productService.deleteProduct(productId);
        return ApiResponse.success(null, "product deleted");
    }
}

