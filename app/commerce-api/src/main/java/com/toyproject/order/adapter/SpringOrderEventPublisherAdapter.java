package com.toyproject.order.adapter;

import com.toyproject.order.application.event.OrderCancelledEvent;
import com.toyproject.order.application.event.OrderCreatedEvent;
import com.toyproject.order.application.port.OrderEventPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SpringOrderEventPublisherAdapter implements OrderEventPort {
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publishOrderCreated(OrderCreatedEvent event) {
        eventPublisher.publishEvent(event);
    }

    @Override
    public void publishOrderCancelled(OrderCancelledEvent event) {
        eventPublisher.publishEvent(event);
    }
}
