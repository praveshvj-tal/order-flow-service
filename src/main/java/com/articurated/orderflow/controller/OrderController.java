package com.articurated.orderflow.controller;

import com.articurated.orderflow.dto.*;
import com.articurated.orderflow.entity.Order;
import com.articurated.orderflow.mapper.OrderMapper;
import com.articurated.orderflow.repository.OrderStateHistoryRepository;
import com.articurated.orderflow.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderStateHistoryRepository historyRepository;

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder() {
        Order created = orderService.createOrder();
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderMapper.toCreateResponse(created));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(OrderMapper.toResponse(orderService.getOrder(orderId)));
    }

    @PutMapping("/{orderId}/state")
    public ResponseEntity<OrderResponse> updateState(@PathVariable Long orderId,
                                                    @Valid @RequestBody UpdateOrderStateRequest request) {
        return ResponseEntity.ok(OrderMapper.toResponse(orderService.updateOrderState(orderId, request.newState())));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancel(@PathVariable Long orderId) {
        return ResponseEntity.ok(OrderMapper.toResponse(orderService.cancelOrder(orderId)));
    }

    @GetMapping("/{orderId}/history")
    public ResponseEntity<List<OrderStateHistoryResponse>> history(@PathVariable Long orderId) {
        return ResponseEntity.ok(historyRepository.findByEntityIdOrderByTimestampAsc(orderId).stream()
                .map(h -> OrderStateHistoryResponse.builder()
                        .entityId(h.getEntityId())
                        .previousState(h.getPreviousState())
                        .newState(h.getNewState())
                        .timestamp(h.getTimestamp())
                        .build())
                .toList());
    }
}

