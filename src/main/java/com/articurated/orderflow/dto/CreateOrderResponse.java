package com.articurated.orderflow.dto;

import com.articurated.orderflow.entity.OrderState;
import lombok.Builder;

@Builder
public record CreateOrderResponse(Long id, OrderState state) {
}

