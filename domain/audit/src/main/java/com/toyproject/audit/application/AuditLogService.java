package com.toyproject.audit.application;

import com.toyproject.audit.domain.AuditEventType;
import com.toyproject.audit.domain.AuditLog;
import com.toyproject.audit.domain.AuditLogRepository;
import com.toyproject.order.application.event.OrderCancelledEvent;
import com.toyproject.order.application.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("dev")
@RequiredArgsConstructor
@Service
public class AuditLogService {
    private final AuditLogRepository auditLogRepository;

    public void recordOrderCreated(OrderCreatedEvent event) {
        auditLogRepository.save(new AuditLog(
            AuditEventType.ORDER_CREATED,
            "ORDER",
            event.orderId().toString(),
            "memberId=%d, productId=%d, quantity=%d, totalPrice=%s".formatted(
                event.memberId(),
                event.productId(),
                event.quantity(),
                event.totalPrice()
            )
        ));
    }

    public void recordOrderCancelled(OrderCancelledEvent event) {
        auditLogRepository.save(new AuditLog(
            AuditEventType.ORDER_CANCELLED,
            "ORDER",
            event.orderId().toString(),
            "memberId=%d, productId=%d, quantity=%d, totalPrice=%s".formatted(
                event.memberId(),
                event.productId(),
                event.quantity(),
                event.totalPrice()
            )
        ));
    }
}
