package com.articurated.orderflow.state_machine;

import com.articurated.orderflow.entity.ReturnState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReturnStateMachineTest {

    @Test
    void allowsHappyPathTransitions() {
        assertDoesNotThrow(() -> ReturnStateMachine.validateTransition(ReturnState.REQUESTED, ReturnState.APPROVED));
        assertDoesNotThrow(() -> ReturnStateMachine.validateTransition(ReturnState.APPROVED, ReturnState.IN_TRANSIT));
        assertDoesNotThrow(() -> ReturnStateMachine.validateTransition(ReturnState.IN_TRANSIT, ReturnState.RECEIVED));
        assertDoesNotThrow(() -> ReturnStateMachine.validateTransition(ReturnState.RECEIVED, ReturnState.COMPLETED));
    }

    @Test
    void allowsRejectFromRequestedOnly() {
        assertDoesNotThrow(() -> ReturnStateMachine.validateTransition(ReturnState.REQUESTED, ReturnState.REJECTED));
        assertThrows(InvalidStateTransitionException.class,
                () -> ReturnStateMachine.validateTransition(ReturnState.APPROVED, ReturnState.REJECTED));
    }

    @Test
    void rejectsInvalidTransition() {
        assertThrows(InvalidStateTransitionException.class,
                () -> ReturnStateMachine.validateTransition(ReturnState.REQUESTED, ReturnState.IN_TRANSIT));
    }
}

