package com.toyproject.order.adapter;

import com.toyproject.catalog.domain.Product;
import com.toyproject.catalog.domain.ProductRepository;
import com.toyproject.order.application.port.dto.ProductSnapshot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductPortAdapterTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductPortAdapter adapter;

    @Test
    @DisplayName("상품이 존재하면 주문용 상품 스냅샷을 반환한다")
    void findById_whenProductExists_returnsSnapshot() {
        Product product = new Product("Keyboard", BigDecimal.valueOf(10000));
        ReflectionTestUtils.setField(product, "id", 10L);
        given(productRepository.findById(10L)).willReturn(Optional.of(product));

        Optional<ProductSnapshot> result = adapter.findById(10L);

        assertThat(result).hasValue(new ProductSnapshot(10L, BigDecimal.valueOf(10000)));
    }

    @Test
    @DisplayName("상품이 존재하지 않으면 빈 Optional을 반환한다")
    void findById_whenProductNotFound_returnsEmpty() {
        given(productRepository.findById(99L)).willReturn(Optional.empty());

        Optional<ProductSnapshot> result = adapter.findById(99L);

        assertThat(result).isEmpty();
    }
}
