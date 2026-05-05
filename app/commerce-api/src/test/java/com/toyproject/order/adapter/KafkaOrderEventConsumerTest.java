package com.toyproject.order.adapter;

import com.toyproject.audit.application.AuditLogService;
import com.toyproject.order.application.event.OrderCancelledEvent;
import com.toyproject.order.application.event.OrderCreatedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;

@ExtendWith(OutputCaptureExtension.class)
@ExtendWith(MockitoExtension.class)
class KafkaOrderEventConsumerTest {
    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private KafkaOrderEventConsumer consumer;

    @Test
    @DisplayName("Kafka 주문 생성 이벤트를 수신하면 감사 로그를 저장하고 처리 로그를 남긴다")
    void consumeOrderCreated_recordsAuditLogAndLogsReceivedEvent(CapturedOutput output) {
        OrderCreatedEvent event = new OrderCreatedEvent(1L, 2L, 3L, 4, BigDecimal.valueOf(40000));

        consumer.consumeOrderCreated(event);

        then(auditLogService).should().recordOrderCreated(event);
        assertThat(output)
            .contains("주문 생성 이벤트를 수신했습니다.")
            .contains("orderId=1")
            .contains("memberId=2")
            .contains("productId=3")
            .contains("quantity=4");
    }

    @Test
    @DisplayName("Kafka 주문 취소 이벤트를 수신하면 감사 로그를 저장하고 처리 로그를 남긴다")
    void consumeOrderCancelled_recordsAuditLogAndLogsReceivedEvent(CapturedOutput output) {
        OrderCancelledEvent event = new OrderCancelledEvent(1L, 2L, 3L, 4, BigDecimal.valueOf(40000));

        consumer.consumeOrderCancelled(event);

        then(auditLogService).should().recordOrderCancelled(event);
        assertThat(output)
            .contains("주문 취소 이벤트를 수신했습니다.")
            .contains("orderId=1")
            .contains("memberId=2")
            .contains("productId=3")
            .contains("quantity=4");
    }
}
