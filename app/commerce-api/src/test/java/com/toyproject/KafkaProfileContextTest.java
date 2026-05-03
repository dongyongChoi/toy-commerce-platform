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
    "spring.profiles.active=dev",
    "spring.kafka.listener.auto-startup=false",
    "spring.cache.type=simple",
    "spring.datasource.url=jdbc:h2:mem:kafka-profile-test;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password="
})
class KafkaProfileContextTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("dev 프로필이 활성화되면 Kafka 주문 이벤트 리스너 빈을 등록한다")
    void devProfileRegistersKafkaOrderEventListener() {
        KafkaOrderEventListener listener = applicationContext.getBean(KafkaOrderEventListener.class);

        assertThat(listener).isNotNull();
    }

    @Test
    @DisplayName("dev 프로필이 활성화되면 Kafka 주문 이벤트 컨슈머 빈을 등록한다")
    void devProfileRegistersKafkaOrderEventConsumer() {
        KafkaOrderEventConsumer consumer = applicationContext.getBean(KafkaOrderEventConsumer.class);

        assertThat(consumer).isNotNull();
    }
}
