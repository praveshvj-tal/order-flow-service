package com.articurated.orderflow.dto;

import com.articurated.orderflow.entity.ReturnState;
import lombok.Builder;

import java.time.Instant;

@Builder
public record ReturnStateHistoryResponse(Long entityId, ReturnState previousState, ReturnState newState, Instant timestamp) {
}

