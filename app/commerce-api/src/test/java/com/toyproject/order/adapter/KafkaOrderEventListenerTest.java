package com.toyproject.order.adapter;

import com.toyproject.order.application.event.OrderCancelledEvent;
import com.toyproject.order.application.event.OrderCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class KafkaOrderEventListenerTest {
    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private KafkaOrderEventListener listener;

    @BeforeEach
    void setUp() {
        listener = new KafkaOrderEventListener(
            kafkaTemplate,
            "toy-commerce.order.created",
            "toy-commerce.order.cancelled"
        );
    }

    @Test
    @DisplayName("주문 생성 이벤트를 Kafka 주문 생성 토픽으로 발행한다")
    void publishOrderCreated_sendsEventToOrderCreatedTopic() {
        OrderCreatedEvent event = new OrderCreatedEvent(1L, 2L, 3L, 4, BigDecimal.valueOf(40000));

        listener.publishOrderCreated(event);

        then(kafkaTemplate).should()
            .send("toy-commerce.order.created", "1", event);
    }

    @Test
    @DisplayName("주문 취소 이벤트를 Kafka 주문 취소 토픽으로 발행한다")
    void publishOrderCancelled_sendsEventToOrderCancelledTopic() {
        OrderCancelledEvent event = new OrderCancelledEvent(1L, 2L, 3L, 4, BigDecimal.valueOf(40000));

        listener.publishOrderCancelled(event);

        then(kafkaTemplate).should()
            .send("toy-commerce.order.cancelled", "1", event);
    }
}
