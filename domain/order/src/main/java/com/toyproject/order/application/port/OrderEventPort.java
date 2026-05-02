package com.toyproject.order.application.port;

import com.toyproject.order.application.event.OrderCancelledEvent;
import com.toyproject.order.application.event.OrderCreatedEvent;

public interface OrderEventPort {
    void publishOrderCreated(OrderCreatedEvent event);

    void publishOrderCancelled(OrderCancelledEvent event);
}
