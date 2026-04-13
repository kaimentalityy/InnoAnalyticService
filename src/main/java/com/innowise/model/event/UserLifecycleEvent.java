package com.innowise.model.event;

import com.innowise.model.enums.EventType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record UserLifecycleEvent(
        String eventId,
        EventType eventType,
        LocalDateTime eventTimestamp,
        String userId,
        String email,
        String ipAddress,
        String userAgent) implements AnalyticsEvent {
    public UserLifecycleEvent {
        if (eventId == null)
            eventId = UUID.randomUUID().toString();
        if (eventType == null)
            eventType = EventType.USER_CREATE;
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
        return "user_lifecycle";
    }
}
