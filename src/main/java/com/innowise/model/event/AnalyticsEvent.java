package com.innowise.model.event;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.innowise.model.enums.EventType;
import java.time.LocalDateTime;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "eventType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = OrderCreatedEvent.class, name = "ORDER_CREATE"),
        @JsonSubTypes.Type(value = PaymentCreatedEvent.class, name = "CREATE_PAYMENT"),
        @JsonSubTypes.Type(value = RatingCreatedEvent.class, name = "RATING_CREATE"),
        @JsonSubTypes.Type(value = UserLifecycleEvent.class, name = "USER_CREATE"),
        @JsonSubTypes.Type(value = UserLifecycleEvent.class, name = "USER_REGISTER"),
        @JsonSubTypes.Type(value = UserLifecycleEvent.class, name = "USER_LOGIN"),
        @JsonSubTypes.Type(value = UserLifecycleEvent.class, name = "CARD_ADDED"),
        @JsonSubTypes.Type(value = UserLifecycleEvent.class, name = "USER_DELETE")
})
public interface AnalyticsEvent {
    String getEventId();

    EventType getEventType();

    @JsonProperty("event_time")
    @JsonAlias("eventTimestamp")
    LocalDateTime getEventTimestamp();

    String getTableName();
}
