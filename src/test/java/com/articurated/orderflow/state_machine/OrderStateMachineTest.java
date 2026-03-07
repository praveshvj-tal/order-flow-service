package com.articurated.orderflow.state_machine;

import com.articurated.orderflow.entity.OrderState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderStateMachineTest {

    @Test
    void allowsHappyPathTransitions() {
        assertDoesNotThrow(() -> OrderStateMachine.validateTransition(OrderState.PENDING_PAYMENT, OrderState.PAID));
        assertDoesNotThrow(() -> OrderStateMachine.validateTransition(OrderState.PAID, OrderState.PROCESSING_IN_WAREHOUSE));
        assertDoesNotThrow(() -> OrderStateMachine.validateTransition(OrderState.PROCESSING_IN_WAREHOUSE, OrderState.SHIPPED));
        assertDoesNotThrow(() -> OrderStateMachine.validateTransition(OrderState.SHIPPED, OrderState.DELIVERED));
    }

    @Test
    void allowsCancellationFromPendingOrPaidOnly() {
        assertDoesNotThrow(() -> OrderStateMachine.validateTransition(OrderState.PENDING_PAYMENT, OrderState.CANCELLED));
        assertDoesNotThrow(() -> OrderStateMachine.validateTransition(OrderState.PAID, OrderState.CANCELLED));

        assertThrows(InvalidStateTransitionException.class,
                () -> OrderStateMachine.validateTransition(OrderState.SHIPPED, OrderState.CANCELLED));
    }

    @Test
    void rejectsAnyOtherTransition() {
        assertThrows(InvalidStateTransitionException.class,
                () -> OrderStateMachine.validateTransition(OrderState.PENDING_PAYMENT, OrderState.SHIPPED));
    }
}

