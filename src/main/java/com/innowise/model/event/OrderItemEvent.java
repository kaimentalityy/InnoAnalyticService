package com.innowise.model.event;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderItemEvent(
                Long itemId,
                String itemName,
                Integer quantity,
                BigDecimal price) {
}
