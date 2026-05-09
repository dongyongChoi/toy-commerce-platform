package com.toyproject.inventory.domain;

import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.Lock;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class StockItemRepositoryTest {
    @Test
    @DisplayName("재고 수정용 상품 조회는 비관적 쓰기 락을 사용한다")
    void findByProductIdForUpdate_usesPessimisticWriteLock() throws NoSuchMethodException {
        Method method = StockItemRepository.class.getMethod("findByProductIdForUpdate", Long.class);

        Lock lock = method.getAnnotation(Lock.class);

        assertThat(lock).isNotNull();
        assertThat(lock.value()).isEqualTo(LockModeType.PESSIMISTIC_WRITE);
    }
}
