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
        String userAgent) {
    public UserLifecycleEvent {
        if (eventId == null)
            eventId = UUID.randomUUID().toString();
        if (eventTimestamp == null)
            eventTimestamp = LocalDateTime.now();
    }
}
