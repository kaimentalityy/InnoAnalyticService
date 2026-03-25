package com.innowise.model.event;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderItemEvent(
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal price) {
}
