package com.toyproject.inventory.domain;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StockItemRepository extends JpaRepository<StockItem, Long> {
    Optional<StockItem> findByProductId(Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from StockItem s where s.productId = :productId")
    Optional<StockItem> findByProductIdForUpdate(@Param("productId") Long productId);
}

