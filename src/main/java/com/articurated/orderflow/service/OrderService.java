package com.articurated.orderflow.service;

import com.articurated.orderflow.background_jobs.InvoiceJobService;
import com.articurated.orderflow.entity.*;
import com.articurated.orderflow.exception.NotFoundException;
import com.articurated.orderflow.repository.OrderRepository;
import com.articurated.orderflow.repository.OrderStateHistoryRepository;
import com.articurated.orderflow.state_machine.OrderStateMachine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderStateHistoryRepository historyRepository;
    private final InvoiceJobService invoiceJobService;

    @Transactional
    public Order createOrder() {
        Order order = Order.builder().state(OrderState.PENDING_PAYMENT).build();
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));
    }

    @Transactional
    public Order updateOrderState(Long orderId, OrderState newState) {
        Order order = getOrder(orderId);
        OrderState previous = order.getState();

        OrderStateMachine.validateTransition(previous, newState);

        order.setState(newState);
        Order saved = orderRepository.save(order);

        historyRepository.save(OrderStateHistory.builder()
                .entityId(saved.getId())
                .previousState(previous)
                .newState(newState)
                .build());

        if (newState == OrderState.SHIPPED) {
            invoiceJobService.generateAndEmailInvoice(saved.getId());
        }

        return saved;
    }

    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = getOrder(orderId);
        OrderState previous = order.getState();

        OrderStateMachine.validateTransition(previous, OrderState.CANCELLED);

        order.setState(OrderState.CANCELLED);
        Order saved = orderRepository.save(order);

        historyRepository.save(OrderStateHistory.builder()
                .entityId(saved.getId())
                .previousState(previous)
                .newState(OrderState.CANCELLED)
                .build());

        return saved;
    }

    @Transactional(readOnly = true)
    public List<OrderStateHistory> getHistory(Long orderId) {
        // Ensure order exists
        getOrder(orderId);
        return historyRepository.findByEntityIdOrderByTimestampAsc(orderId);
    }
}

