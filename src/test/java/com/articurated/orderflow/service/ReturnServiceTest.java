package com.articurated.orderflow.service;

import com.articurated.orderflow.background_jobs.RefundJobService;
import com.articurated.orderflow.entity.*;
import com.articurated.orderflow.exception.BadRequestException;
import com.articurated.orderflow.repository.ReturnRequestRepository;
import com.articurated.orderflow.repository.ReturnStateHistoryRepository;
import com.articurated.orderflow.state_machine.InvalidStateTransitionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReturnServiceTest {

    private ReturnRequestRepository returnRequestRepository;
    private ReturnStateHistoryRepository historyRepository;
    private OrderService orderService;
    private RefundJobService refundJobService;

    private ReturnService returnService;

    @BeforeEach
    void setUp() {
        returnRequestRepository = mock(ReturnRequestRepository.class);
        historyRepository = mock(ReturnStateHistoryRepository.class);
        orderService = mock(OrderService.class);
        refundJobService = mock(RefundJobService.class);
        returnService = new ReturnService(returnRequestRepository, historyRepository, orderService, refundJobService);
    }

    @Test
    void initiateReturnOnlyForDeliveredOrder() {
        Order notDelivered = Order.builder().id(1L).state(OrderState.SHIPPED).build();
        when(orderService.getOrder(1L)).thenReturn(notDelivered);

        assertThrows(BadRequestException.class, () -> returnService.initiateReturn(1L));
    }

    @Test
    void completeReturnTriggersRefundJob() {
        Order delivered = Order.builder().id(2L).state(OrderState.DELIVERED).build();
        ReturnRequest rr = ReturnRequest.builder().id(20L).order(delivered).state(ReturnState.RECEIVED).build();

        when(returnRequestRepository.findById(20L)).thenReturn(Optional.of(rr));
        when(returnRequestRepository.save(any(ReturnRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        returnService.updateReturnState(20L, ReturnState.COMPLETED);

        verify(refundJobService, times(1)).processRefund(20L);
        verify(historyRepository, times(1)).save(any());
    }

    @Test
    void illegalReturnTransitionThrows() {
        Order delivered = Order.builder().id(3L).state(OrderState.DELIVERED).build();
        ReturnRequest rr = ReturnRequest.builder().id(30L).order(delivered).state(ReturnState.REQUESTED).build();

        when(returnRequestRepository.findById(30L)).thenReturn(Optional.of(rr));

        assertThrows(InvalidStateTransitionException.class,
                () -> returnService.updateReturnState(30L, ReturnState.RECEIVED));

        verify(returnRequestRepository, never()).save(any());
    }
}

