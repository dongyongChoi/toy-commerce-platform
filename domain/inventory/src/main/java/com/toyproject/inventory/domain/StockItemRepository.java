package com.toyproject.inventory.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StockItemRepository extends JpaRepository<StockItem, Long> {
    Optional<StockItem> findByProductId(Long productId);

    boolean existsByProductId(Long productId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update StockItem s
        set s.quantity = s.quantity - :quantity
        where s.productId = :productId
          and s.quantity >= :quantity
        """)
    int deductIfEnough(@Param("productId") Long productId, @Param("quantity") int quantity);
}
