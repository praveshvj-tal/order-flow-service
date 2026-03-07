package com.articurated.orderflow.state_machine;

import com.articurated.orderflow.entity.ReturnState;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class ReturnStateMachine {

    private static final Map<ReturnState, Set<ReturnState>> ALLOWED = Map.of(
            ReturnState.REQUESTED, EnumSet.of(ReturnState.APPROVED, ReturnState.REJECTED),
            ReturnState.APPROVED, EnumSet.of(ReturnState.IN_TRANSIT),
            ReturnState.REJECTED, EnumSet.noneOf(ReturnState.class),
            ReturnState.IN_TRANSIT, EnumSet.of(ReturnState.RECEIVED),
            ReturnState.RECEIVED, EnumSet.of(ReturnState.COMPLETED),
            ReturnState.COMPLETED, EnumSet.noneOf(ReturnState.class)
    );

    public static void validateTransition(ReturnState from, ReturnState to) {
        if (from == null || to == null) {
            throw new InvalidStateTransitionException("Return transition states must be non-null");
        }
        Set<ReturnState> allowedTargets = ALLOWED.getOrDefault(from, Set.of());
        if (!allowedTargets.contains(to)) {
            throw new InvalidStateTransitionException("Illegal return transition: " + from + " -> " + to);
        }
    }
}

