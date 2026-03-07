package com.articurated.orderflow.state_machine;

import com.articurated.orderflow.entity.OrderState;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class OrderStateMachine {

    private static final Map<OrderState, Set<OrderState>> ALLOWED = Map.of(
            OrderState.PENDING_PAYMENT, EnumSet.of(OrderState.PAID, OrderState.CANCELLED),
            OrderState.PAID, EnumSet.of(OrderState.PROCESSING_IN_WAREHOUSE, OrderState.CANCELLED),
            OrderState.PROCESSING_IN_WAREHOUSE, EnumSet.of(OrderState.SHIPPED),
            OrderState.SHIPPED, EnumSet.of(OrderState.DELIVERED),
            OrderState.DELIVERED, EnumSet.noneOf(OrderState.class),
            OrderState.CANCELLED, EnumSet.noneOf(OrderState.class)
    );

    public static void validateTransition(OrderState from, OrderState to) {
        if (from == null || to == null) {
            throw new InvalidStateTransitionException("Order transition states must be non-null");
        }
        Set<OrderState> allowedTargets = ALLOWED.getOrDefault(from, Set.of());
        if (!allowedTargets.contains(to)) {
            throw new InvalidStateTransitionException("Illegal order transition: " + from + " -> " + to);
        }
    }
}

