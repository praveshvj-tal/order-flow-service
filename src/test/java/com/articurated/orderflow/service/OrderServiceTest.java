package com.articurated.orderflow.service;

import com.articurated.orderflow.background_jobs.InvoiceJobService;
import com.articurated.orderflow.entity.Order;
import com.articurated.orderflow.entity.OrderState;
import com.articurated.orderflow.repository.OrderRepository;
import com.articurated.orderflow.repository.OrderStateHistoryRepository;
import com.articurated.orderflow.state_machine.InvalidStateTransitionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    private OrderRepository orderRepository;
    private OrderStateHistoryRepository historyRepository;
    private InvoiceJobService invoiceJobService;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        historyRepository = mock(OrderStateHistoryRepository.class);
        invoiceJobService = mock(InvoiceJobService.class);
        orderService = new OrderService(orderRepository, historyRepository, invoiceJobService);
    }

    @Test
    void updateStateToShippedTriggersInvoiceJob() {
        Order order = Order.builder().id(10L).state(OrderState.PROCESSING_IN_WAREHOUSE).build();
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        orderService.updateOrderState(10L, OrderState.SHIPPED);

        verify(invoiceJobService, times(1)).generateAndEmailInvoice(10L);
        verify(historyRepository, times(1)).save(any());
    }

    @Test
    void illegalTransitionThrows() {
        Order order = Order.builder().id(11L).state(OrderState.PENDING_PAYMENT).build();
        when(orderRepository.findById(11L)).thenReturn(Optional.of(order));

        assertThrows(InvalidStateTransitionException.class,
                () -> orderService.updateOrderState(11L, OrderState.DELIVERED));

        verify(orderRepository, never()).save(any());
    }

    @Test
    void cancelFromPaidAllowed() {
        Order order = Order.builder().id(12L).state(OrderState.PAID).build();
        when(orderRepository.findById(12L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order cancelled = orderService.cancelOrder(12L);

        assertEquals(OrderState.CANCELLED, cancelled.getState());
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        assertEquals(OrderState.CANCELLED, captor.getValue().getState());
        verify(historyRepository).save(any());
    }
}

