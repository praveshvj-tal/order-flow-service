package com.articurated.orderflow.dto;

import com.articurated.orderflow.entity.OrderState;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStateRequest(@NotNull OrderState newState) {
}

