package com.articurated.orderflow.entity;

public enum OrderState {
    PENDING_PAYMENT,
    PAID,
    PROCESSING_IN_WAREHOUSE,
    SHIPPED,
    DELIVERED,
    CANCELLED
}

