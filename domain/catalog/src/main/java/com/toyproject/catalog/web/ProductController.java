package com.toyproject.catalog.web;

import com.toyproject.catalog.application.ProductService;
import com.toyproject.catalog.web.dto.CreateProductRequest;
import com.toyproject.catalog.web.dto.ProductResponse;
import com.toyproject.common.web.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        return ApiResponse.success(productService.createProduct(request), "product created");
    }
}

