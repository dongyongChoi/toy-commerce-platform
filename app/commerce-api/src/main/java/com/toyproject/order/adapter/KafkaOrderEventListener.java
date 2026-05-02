package com.toyproject.order.adapter;

import com.toyproject.order.application.event.OrderCancelledEvent;
import com.toyproject.order.application.event.OrderCreatedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Profile("kafka")
@Component
public class KafkaOrderEventListener {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String orderCreatedTopic;
    private final String orderCancelledTopic;

    public KafkaOrderEventListener(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${toy-commerce.kafka.topics.order-created}") String orderCreatedTopic,
            @Value("${toy-commerce.kafka.topics.order-cancelled}") String orderCancelledTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.orderCreatedTopic = orderCreatedTopic;
        this.orderCancelledTopic = orderCancelledTopic;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishOrderCreated(OrderCreatedEvent event) {
        kafkaTemplate.send(orderCreatedTopic, event.orderId().toString(), event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishOrderCancelled(OrderCancelledEvent event) {
        kafkaTemplate.send(orderCancelledTopic, event.orderId().toString(), event);
    }
}