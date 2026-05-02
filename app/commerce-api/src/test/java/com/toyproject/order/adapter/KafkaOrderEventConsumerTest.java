package com.toyproject.order.adapter;

import com.toyproject.order.application.event.OrderCancelledEvent;
import com.toyproject.order.application.event.OrderCreatedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
class KafkaOrderEventConsumerTest {
    private final KafkaOrderEventConsumer consumer = new KafkaOrderEventConsumer();

    @Test
    @DisplayName("Kafka 주문 생성 이벤트를 수신하면 처리 로그를 남긴다")
    void consumeOrderCreated_logsReceivedEvent(CapturedOutput output) {
        OrderCreatedEvent event = new OrderCreatedEvent(1L, 2L, 3L, 4, BigDecimal.valueOf(40000));

        consumer.consumeOrderCreated(event);

        assertThat(output)
            .contains("주문 생성 이벤트를 수신했습니다.")
            .contains("orderId=1")
            .contains("memberId=2")
            .contains("productId=3")
            .contains("quantity=4");
    }

    @Test
    @DisplayName("Kafka 주문 취소 이벤트를 수신하면 처리 로그를 남긴다")
    void consumeOrderCancelled_logsReceivedEvent(CapturedOutput output) {
        OrderCancelledEvent event = new OrderCancelledEvent(1L, 2L, 3L, 4, BigDecimal.valueOf(40000));

        consumer.consumeOrderCancelled(event);

        assertThat(output)
            .contains("주문 취소 이벤트를 수신했습니다.")
            .contains("orderId=1")
            .contains("memberId=2")
            .contains("productId=3")
            .contains("quantity=4");
    }
}
