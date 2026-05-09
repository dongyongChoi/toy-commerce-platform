package com.toyproject.inventory.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(
    properties = {
        "spring.datasource.url=jdbc:h2:mem:stock-item-repository-test;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"
    }
)
@Import(StockItemRepositoryTest.TestApplication.class)
class StockItemRepositoryTest {
    @Autowired
    private StockItemRepository stockItemRepository;

    @Test
    @DisplayName("재고가 충분하면 조건부 차감 쿼리로 수량을 차감한다")
    void deductIfEnough_whenStockEnough_updatesQuantity() {
        stockItemRepository.saveAndFlush(new StockItem(10L, 5));

        int updatedCount = stockItemRepository.deductIfEnough(10L, 3);
        stockItemRepository.flush();

        StockItem stockItem = stockItemRepository.findByProductId(10L).orElseThrow();
        assertThat(updatedCount).isEqualTo(1);
        assertThat(stockItem.getQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("재고가 부족하면 조건부 차감 쿼리로 수량을 변경하지 않는다")
    void deductIfEnough_whenStockInsufficient_doesNotUpdateQuantity() {
        stockItemRepository.saveAndFlush(new StockItem(10L, 2));

        int updatedCount = stockItemRepository.deductIfEnough(10L, 3);
        stockItemRepository.flush();

        StockItem stockItem = stockItemRepository.findByProductId(10L).orElseThrow();
        assertThat(updatedCount).isZero();
        assertThat(stockItem.getQuantity()).isEqualTo(2);
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EntityScan("com.toyproject.inventory.domain")
    @EnableJpaRepositories("com.toyproject.inventory.domain")
    static class TestApplication {
    }
}
