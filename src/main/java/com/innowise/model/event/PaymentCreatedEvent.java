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
        PaymentStatus status) {
    public PaymentCreatedEvent {
        if (eventId == null)
            eventId = UUID.randomUUID().toString();
        if (eventType == null)
            eventType = EventType.CREATE_PAYMENT;
        if (eventTimestamp == null)
            eventTimestamp = LocalDateTime.now();
    }
}
