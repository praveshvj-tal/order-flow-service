package com.articurated.orderflow.dto;

import com.articurated.orderflow.entity.OrderState;
import lombok.Builder;

import java.time.Instant;

@Builder
public record OrderResponse(Long id, OrderState state, Instant createdAt) {
}

