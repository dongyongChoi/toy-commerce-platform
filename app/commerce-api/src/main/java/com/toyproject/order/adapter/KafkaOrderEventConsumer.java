package com.toyproject.order.adapter;

import com.toyproject.audit.application.AuditLogService;
import com.toyproject.order.application.event.OrderCancelledEvent;
import com.toyproject.order.application.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Profile("dev")
@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaOrderEventConsumer {
    private final AuditLogService auditLogService;

    @KafkaListener(topics = "${toy-commerce.kafka.topics.order-created}")
    public void consumeOrderCreated(OrderCreatedEvent event) {
        auditLogService.recordOrderCreated(event);
        log.info(
            "주문 생성 이벤트를 수신했습니다. orderId={}, memberId={}, productId={}, quantity={}",
            event.orderId(),
            event.memberId(),
            event.productId(),
            event.quantity()
        );
    }

    @KafkaListener(topics = "${toy-commerce.kafka.topics.order-cancelled}")
    public void consumeOrderCancelled(OrderCancelledEvent event) {
        auditLogService.recordOrderCancelled(event);
        log.info(
            "주문 취소 이벤트를 수신했습니다. orderId={}, memberId={}, productId={}, quantity={}",
            event.orderId(),
            event.memberId(),
            event.productId(),
            event.quantity()
        );
    }
}
