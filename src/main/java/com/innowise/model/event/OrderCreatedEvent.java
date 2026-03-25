package com.innowise.model.event;

import com.innowise.model.enums.EventType;
import com.innowise.model.enums.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record OrderCreatedEvent(
        String eventId,
        EventType eventType,
        LocalDateTime eventTimestamp,
        Long orderId,
        String userId,
        String userEmail,
        OrderStatus status,
        BigDecimal totalAmount,
        List<OrderItemEvent> items) {
    public OrderCreatedEvent {
        if (eventId == null)
            eventId = UUID.randomUUID().toString();
        if (eventType == null)
            eventType = EventType.ORDER_CREATE;
        if (eventTimestamp == null)
            eventTimestamp = LocalDateTime.now();
    }
}
