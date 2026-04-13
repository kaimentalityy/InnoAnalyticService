package com.innowise.service.kafka;

import com.innowise.model.event.AnalyticsEvent;
import com.innowise.service.UniversalAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UniversalEventConsumer {

    private final UniversalAnalyticsService universalAnalyticsService;

    @KafkaListener(topics = "${spring.kafka.topic.analytics}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeAnalyticsEvent(AnalyticsEvent event) {
        log.info("Received analytics event: {} for table: {}", event.getEventType(), event.getTableName());
        universalAnalyticsService.addToBuffer(event);
    }
}
