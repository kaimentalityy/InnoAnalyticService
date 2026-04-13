package com.innowise.model.event;

import com.innowise.model.enums.EventType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record RatingCreatedEvent(
        String eventId,
        EventType eventType,
        LocalDateTime eventTimestamp,
        UUID ratingId,
        Long orderId,
        String targetId,
        Integer score,
        String comment) implements AnalyticsEvent {
    public RatingCreatedEvent {
        if (eventId == null)
            eventId = UUID.randomUUID().toString();
        if (eventType == null)
            eventType = EventType.RATING_CREATE;
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
        return "ratings_analytics";
    }
}