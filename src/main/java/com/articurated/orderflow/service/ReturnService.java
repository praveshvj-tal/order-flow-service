package com.articurated.orderflow.service;

import com.articurated.orderflow.background_jobs.RefundJobService;
import com.articurated.orderflow.entity.*;
import com.articurated.orderflow.exception.BadRequestException;
import com.articurated.orderflow.exception.NotFoundException;
import com.articurated.orderflow.repository.ReturnRequestRepository;
import com.articurated.orderflow.repository.ReturnStateHistoryRepository;
import com.articurated.orderflow.state_machine.ReturnStateMachine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReturnService {

    private final ReturnRequestRepository returnRequestRepository;
    private final ReturnStateHistoryRepository historyRepository;
    private final OrderService orderService;
    private final RefundJobService refundJobService;

    @Transactional
    public ReturnRequest initiateReturn(Long orderId) {
        Order order = orderService.getOrder(orderId);
        if (order.getState() != OrderState.DELIVERED) {
            throw new BadRequestException("Return can only be initiated for DELIVERED orders");
        }

        ReturnRequest rr = ReturnRequest.builder()
                .order(order)
                .state(ReturnState.REQUESTED)
                .build();

        ReturnRequest saved = returnRequestRepository.save(rr);

        // No previous state in creation; audit wants previous/new. Use REQUESTED->REQUESTED to reflect init.
        historyRepository.save(ReturnStateHistory.builder()
                .entityId(saved.getId())
                .previousState(ReturnState.REQUESTED)
                .newState(ReturnState.REQUESTED)
                .build());

        return saved;
    }

    @Transactional(readOnly = true)
    public ReturnRequest getReturnRequest(Long returnId) {
        return returnRequestRepository.findById(returnId)
                .orElseThrow(() -> new NotFoundException("Return request not found: " + returnId));
    }

    @Transactional
    public ReturnRequest updateReturnState(Long returnId, ReturnState newState) {
        ReturnRequest rr = getReturnRequest(returnId);
        ReturnState previous = rr.getState();

        ReturnStateMachine.validateTransition(previous, newState);

        rr.setState(newState);
        ReturnRequest saved = returnRequestRepository.save(rr);

        historyRepository.save(ReturnStateHistory.builder()
                .entityId(saved.getId())
                .previousState(previous)
                .newState(newState)
                .build());

        if (newState == ReturnState.COMPLETED) {
            refundJobService.processRefund(saved.getId());
        }

        return saved;
    }

    @Transactional(readOnly = true)
    public List<ReturnStateHistory> getHistory(Long returnId) {
        getReturnRequest(returnId);
        return historyRepository.findByEntityIdOrderByTimestampAsc(returnId);
    }
}

