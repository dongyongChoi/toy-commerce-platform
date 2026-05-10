package com.toyproject.order.adapter;

import com.toyproject.order.application.event.OrderCancelledEvent;
import com.toyproject.order.application.event.OrderConfirmedEvent;
import com.toyproject.order.application.event.OrderCreatedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class SpringOrderEventPublisherAdapterTest {
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private SpringOrderEventPublisherAdapter adapter;

    @Test
    @DisplayName("주문 생성 이벤트를 Spring 이벤트로 발행한다")
    void publishOrderCreated_publishesSpringEvent() {
        OrderCreatedEvent event = new OrderCreatedEvent(1L, 2L, 3L, 4, BigDecimal.valueOf(40000));

        adapter.publishOrderCreated(event);

        then(eventPublisher).should().publishEvent(event);
    }

    @Test
    @DisplayName("주문 확정 이벤트를 Spring 이벤트로 발행한다")
    void publishOrderConfirmed_publishesSpringEvent() {
        OrderConfirmedEvent event = new OrderConfirmedEvent(1L, 2L, 3L, 4, BigDecimal.valueOf(40000));

        adapter.publishOrderConfirmed(event);

        then(eventPublisher).should().publishEvent(event);
    }

    @Test
    @DisplayName("주문 취소 이벤트를 Spring 이벤트로 발행한다")
    void publishOrderCancelled_publishesSpringEvent() {
        OrderCancelledEvent event = new OrderCancelledEvent(1L, 2L, 3L, 4, BigDecimal.valueOf(40000));

        adapter.publishOrderCancelled(event);

        then(eventPublisher).should().publishEvent(event);
    }
}
