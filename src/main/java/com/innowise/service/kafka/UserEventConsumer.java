package com.innowise.service.kafka;

import com.innowise.model.event.UserLifecycleEvent;
import com.innowise.service.AnalyticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserEventConsumer {

    private final AnalyticService analyticService;

    @KafkaListener(topics = "${spring.kafka.topic.user-lifecycle}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUserLifecycleEvent(UserLifecycleEvent event) {
        log.info("Received user lifecycle event: {} for user: {}", event.eventType(), event.userId());
        analyticService.addToBuffer(event);
    }
}
