package com.toyproject.catalog.application;

import com.toyproject.catalog.domain.Product;
import com.toyproject.catalog.domain.ProductRepository;
import com.toyproject.catalog.web.dto.CreateProductRequest;
import com.toyproject.catalog.web.dto.ProductResponse;
import com.toyproject.catalog.web.dto.UpdateProductRequest;
import com.toyproject.common.core.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Captor
    private ArgumentCaptor<Product> productCaptor;

    @Test
    @DisplayName("상품 목록을 조회하면 상품 응답 목록으로 변환해 반환한다")
    void getProducts_returnsMappedResponses() {
        Product firstProduct = new Product("Keyboard", BigDecimal.valueOf(10000));
        Product secondProduct = new Product("Mouse", BigDecimal.valueOf(5000));
        ReflectionTestUtils.setField(firstProduct, "id", 1L);
        ReflectionTestUtils.setField(secondProduct, "id", 2L);

        given(productRepository.findAll()).willReturn(List.of(firstProduct, secondProduct));

        List<ProductResponse> result = productService.getProducts();

        assertThat(result).containsExactly(
            new ProductResponse(1L, "Keyboard", BigDecimal.valueOf(10000)),
            new ProductResponse(2L, "Mouse", BigDecimal.valueOf(5000))
        );
    }

    @Test
    @DisplayName("상품을 생성하면 저장된 상품 응답을 반환한다")
    void createProduct_savesProductAndReturnsResponse() {
        CreateProductRequest request = new CreateProductRequest("Keyboard", BigDecimal.valueOf(10000));
        given(productRepository.save(any(Product.class))).willAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            ReflectionTestUtils.setField(product, "id", 1L);
            return product;
        });

        ProductResponse result = productService.createProduct(request);

        then(productRepository).should().save(productCaptor.capture());
        Product savedProduct = productCaptor.getValue();
        assertThat(savedProduct.getName()).isEqualTo("Keyboard");
        assertThat(savedProduct.getPrice()).isEqualByComparingTo("10000");
        assertThat(result).isEqualTo(new ProductResponse(1L, "Keyboard", BigDecimal.valueOf(10000)));
    }

    @Test
    @DisplayName("상품 단건을 조회하면 상품 응답을 반환한다")
    void getProduct_returnsProductResponse() {
        Product product = new Product("Keyboard", BigDecimal.valueOf(10000));
        ReflectionTestUtils.setField(product, "id", 1L);
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        ProductResponse result = productService.getProduct(1L);

        assertThat(result).isEqualTo(new ProductResponse(1L, "Keyboard", BigDecimal.valueOf(10000)));
    }

    @Test
    @DisplayName("존재하지 않는 상품을 조회하면 예외가 발생한다")
    void getProduct_whenMissing_throwsDomainException() {
        given(productRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProduct(999L))
            .isInstanceOf(DomainException.class)
            .hasMessage("product not found");
    }

    @Test
    @DisplayName("상품을 수정하면 변경된 상품 응답을 반환한다")
    void updateProduct_updatesProductAndReturnsResponse() {
        Product product = new Product("Keyboard", BigDecimal.valueOf(10000));
        ReflectionTestUtils.setField(product, "id", 1L);
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        ProductResponse result = productService.updateProduct(
            1L,
            new UpdateProductRequest("Keyboard Pro", BigDecimal.valueOf(15000))
        );

        assertThat(product.getName()).isEqualTo("Keyboard Pro");
        assertThat(product.getPrice()).isEqualByComparingTo("15000");
        assertThat(result).isEqualTo(new ProductResponse(1L, "Keyboard Pro", BigDecimal.valueOf(15000)));
    }

    @Test
    @DisplayName("상품을 삭제하면 저장소에서 제거한다")
    void deleteProduct_deletesProduct() {
        Product product = new Product("Keyboard", BigDecimal.valueOf(10000));
        ReflectionTestUtils.setField(product, "id", 1L);
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        productService.deleteProduct(1L);

        then(productRepository).should().delete(product);
    }
}
