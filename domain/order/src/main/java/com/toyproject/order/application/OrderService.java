package com.toyproject.order.application;

import com.toyproject.common.core.DomainException;
import com.toyproject.common.core.ErrorCode;
import com.toyproject.order.domain.PurchaseOrder;
import com.toyproject.order.domain.PurchaseOrderRepository;
import com.toyproject.order.web.dto.CreateOrderRequest;
import com.toyproject.order.web.dto.OrderResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OrderService {
    private final PurchaseOrderRepository purchaseOrderRepository;

    public OrderService(PurchaseOrderRepository purchaseOrderRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    public List<OrderResponse> getOrders() {
        return purchaseOrderRepository.findAll()
            .stream()
            .map(OrderResponse::from)
            .toList();
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        if (request.quantity() <= 0) {
            throw new DomainException(ErrorCode.INVALID_INPUT, "quantity must be greater than zero");
        }

        PurchaseOrder order = purchaseOrderRepository.save(
            new PurchaseOrder(
                request.memberId(),
                request.productId(),
                request.quantity(),
                request.totalPrice()
            )
        );

        return OrderResponse.from(order);
    }
}

