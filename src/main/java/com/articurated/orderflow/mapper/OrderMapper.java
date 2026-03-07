package com.articurated.orderflow.mapper;

import com.articurated.orderflow.dto.CreateOrderResponse;
import com.articurated.orderflow.dto.OrderResponse;
import com.articurated.orderflow.entity.Order;

public class OrderMapper {
    private OrderMapper() {
    }

    public static CreateOrderResponse toCreateResponse(Order order) {
        return CreateOrderResponse.builder()
                .id(order.getId())
                .state(order.getState())
                .build();
    }

    public static OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .state(order.getState())
                .createdAt(order.getCreatedAt())
                .build();
    }
}

