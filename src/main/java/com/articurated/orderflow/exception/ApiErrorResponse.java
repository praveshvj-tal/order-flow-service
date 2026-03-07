package com.articurated.orderflow.exception;

import lombok.Builder;

import java.time.Instant;

@Builder
public record ApiErrorResponse(String message, Instant timestamp) {
}

