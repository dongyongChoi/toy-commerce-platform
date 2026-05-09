package com.toyproject.inventory.application;

import com.toyproject.common.core.DomainException;
import com.toyproject.inventory.domain.StockItem;
import com.toyproject.inventory.domain.StockItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    classes = InventoryServiceConcurrencyTest.TestApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = {
        "spring.datasource.url=jdbc:h2:mem:inventory-concurrency-test;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.open-in-view=false"
    }
)
class InventoryServiceConcurrencyTest {
    private static final long PRODUCT_ID = 10L;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private StockItemRepository stockItemRepository;

    @Test
    @DisplayName("동시에 재고를 차감해도 보유 재고 수량만큼만 성공한다")
    void deductStock_concurrently_allowsOnlyAvailableStock() throws Exception {
        stockItemRepository.saveAndFlush(new StockItem(PRODUCT_ID, 5));
        int requestCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(requestCount);
        CountDownLatch readyLatch = new CountDownLatch(requestCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger insufficientStockCount = new AtomicInteger();
        ConcurrentLinkedQueue<Throwable> unexpectedErrors = new ConcurrentLinkedQueue<>();
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < requestCount; i++) {
            futures.add(executorService.submit(() -> {
                readyLatch.countDown();
                startLatch.await(5, TimeUnit.SECONDS);

                try {
                    inventoryService.deductStock(PRODUCT_ID, 1);
                    successCount.incrementAndGet();
                } catch (DomainException exception) {
                    if ("재고가 부족합니다.".equals(exception.getMessage())) {
                        insufficientStockCount.incrementAndGet();
                    } else {
                        unexpectedErrors.add(exception);
                    }
                } catch (Throwable throwable) {
                    unexpectedErrors.add(throwable);
                }
                return null;
            }));
        }

        assertThat(readyLatch.await(5, TimeUnit.SECONDS)).isTrue();
        startLatch.countDown();
        for (Future<?> future : futures) {
            future.get(10, TimeUnit.SECONDS);
        }
        executorService.shutdown();
        assertThat(executorService.awaitTermination(5, TimeUnit.SECONDS)).isTrue();

        StockItem stockItem = stockItemRepository.findByProductId(PRODUCT_ID).orElseThrow();
        assertThat(unexpectedErrors).isEmpty();
        assertThat(successCount.get()).isEqualTo(5);
        assertThat(insufficientStockCount.get()).isEqualTo(5);
        assertThat(stockItem.getQuantity()).isZero();
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import(InventoryService.class)
    @EntityScan("com.toyproject.inventory.domain")
    @EnableJpaRepositories("com.toyproject.inventory.domain")
    static class TestApplication {
    }
}
