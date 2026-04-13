package com.innowise.model.event;

import com.innowise.model.enums.EventType;
import com.innowise.model.enums.PaymentStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record PaymentCreatedEvent(
        String eventId,
        EventType eventType,
        LocalDateTime eventTimestamp,
        String paymentId,
        Long orderId,
        String userId,
        BigDecimal amount,
        PaymentStatus status) implements AnalyticsEvent {
    public PaymentCreatedEvent {
        if (eventId == null)
            eventId = UUID.randomUUID().toString();
        if (eventType == null)
            eventType = EventType.CREATE_PAYMENT;
        if (eventTimestamp == null)
            eventTimestamp = LocalDateTime.now();
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public EventType getEventType() {
        return eventType;
    }

    @Override
    public LocalDateTime getEventTimestamp() {
        return eventTimestamp;
    }

    @Override
    public String getTableName() {
        return "payment_analytics";
    }
}
