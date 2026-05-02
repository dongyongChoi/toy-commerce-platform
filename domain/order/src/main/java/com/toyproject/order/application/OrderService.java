package com.toyproject.order.application;

import com.toyproject.common.core.DomainException;
import com.toyproject.common.core.ErrorCode;
import com.toyproject.order.application.port.MemberPort;
import com.toyproject.order.application.port.ProductPort;
import com.toyproject.order.application.port.StockPort;
import com.toyproject.order.domain.PurchaseOrder;
import com.toyproject.order.domain.PurchaseOrderRepository;
import com.toyproject.order.web.dto.CreateOrderRequest;
import com.toyproject.order.web.dto.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OrderService {
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final MemberPort memberPort;
    private final ProductPort productPort;
    private final StockPort stockPort;

    public List<OrderResponse> getOrders() {
        return purchaseOrderRepository.findAll()
            .stream()
            .map(OrderResponse::from)
            .toList();
    }

    public OrderResponse getOrder(Long orderId) {
        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
            .orElseThrow(() -> new DomainException(ErrorCode.RESOURCE_NOT_FOUND, "주문을 찾을 수 없습니다."));
        return OrderResponse.from(order);
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        if (!memberPort.exists(request.memberId())) {
            throw new DomainException(ErrorCode.RESOURCE_NOT_FOUND, "회원을 찾을 수 없습니다.");
        }
        if (!productPort.exists(request.productId())) {
            throw new DomainException(ErrorCode.RESOURCE_NOT_FOUND, "상품을 찾을 수 없습니다.");
        }
        stockPort.deduct(request.productId(), request.quantity());

        PurchaseOrder order = purchaseOrderRepository.save(
            new PurchaseOrder(request.memberId(), request.productId(), request.quantity(), request.totalPrice())
        );
        return OrderResponse.from(order);
    }

    @Transactional
    public OrderResponse confirmOrder(Long orderId) {
        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
            .orElseThrow(() -> new DomainException(ErrorCode.RESOURCE_NOT_FOUND, "주문을 찾을 수 없습니다."));
        order.confirm();
        return OrderResponse.from(order);
    }

    @Transactional
    public OrderResponse cancelOrder(Long orderId) {
        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
            .orElseThrow(() -> new DomainException(ErrorCode.RESOURCE_NOT_FOUND, "주문을 찾을 수 없습니다."));
        order.cancel();
        stockPort.restore(order.getProductId(), order.getQuantity());
        return OrderResponse.from(order);
    }
}
