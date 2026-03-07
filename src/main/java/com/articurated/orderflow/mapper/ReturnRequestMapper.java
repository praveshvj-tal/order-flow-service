package com.articurated.orderflow.mapper;

import com.articurated.orderflow.dto.ReturnRequestResponse;
import com.articurated.orderflow.entity.ReturnRequest;

public class ReturnRequestMapper {
    private ReturnRequestMapper() {
    }

    public static ReturnRequestResponse toResponse(ReturnRequest rr) {
        return ReturnRequestResponse.builder()
                .id(rr.getId())
                .orderId(rr.getOrder().getId())
                .state(rr.getState())
                .createdAt(rr.getCreatedAt())
                .build();
    }
}

