package com.articurated.orderflow.dto;

import com.articurated.orderflow.entity.ReturnState;
import lombok.Builder;

import java.time.Instant;

@Builder
public record ReturnRequestResponse(Long id, Long orderId, ReturnState state, Instant createdAt) {
}

