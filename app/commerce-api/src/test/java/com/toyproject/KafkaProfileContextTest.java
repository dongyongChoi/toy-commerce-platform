package com.toyproject;

import com.toyproject.order.adapter.KafkaOrderEventConsumer;
import com.toyproject.order.adapter.KafkaOrderEventListener;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
    "spring.profiles.active=local,kafka",
    "spring.kafka.listener.auto-startup=false"
})
class KafkaProfileContextTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("kafka 프로필이 활성화되면 Kafka 주문 이벤트 리스너 빈을 등록한다")
    void kafkaProfileRegistersKafkaOrderEventListener() {
        KafkaOrderEventListener listener = applicationContext.getBean(KafkaOrderEventListener.class);

        assertThat(listener).isNotNull();
    }

    @Test
    @DisplayName("kafka 프로필이 활성화되면 Kafka 주문 이벤트 컨슈머 빈을 등록한다")
    void kafkaProfileRegistersKafkaOrderEventConsumer() {
        KafkaOrderEventConsumer consumer = applicationContext.getBean(KafkaOrderEventConsumer.class);

        assertThat(consumer).isNotNull();
    }
}
