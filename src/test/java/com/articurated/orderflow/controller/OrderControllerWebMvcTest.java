package com.articurated.orderflow.controller;

import com.articurated.orderflow.dto.UpdateOrderStateRequest;
import com.articurated.orderflow.entity.Order;
import com.articurated.orderflow.entity.OrderState;
import com.articurated.orderflow.entity.OrderStateHistory;
import com.articurated.orderflow.repository.OrderStateHistoryRepository;
import com.articurated.orderflow.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerWebMvcTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    OrderService orderService;

    @MockBean
    OrderStateHistoryRepository historyRepository;

    @Test
    void createOrder_returns201AndBody() throws Exception {
        Order created = Order.builder().id(1L).state(OrderState.PENDING_PAYMENT).build();
        BDDMockito.given(orderService.createOrder()).willReturn(created);

        mockMvc.perform(post("/orders"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.state").value("PENDING_PAYMENT"));
    }

    @Test
    void getOrder_returns200AndBody() throws Exception {
        Order order = Order.builder().id(5L).state(OrderState.PAID).build();
        BDDMockito.given(orderService.getOrder(5L)).willReturn(order);

        mockMvc.perform(get("/orders/5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.state").value("PAID"));
    }

    @Test
    void updateState_validRequest_returns200() throws Exception {
        Order updated = Order.builder().id(7L).state(OrderState.SHIPPED).build();
        BDDMockito.given(orderService.updateOrderState(7L, OrderState.SHIPPED)).willReturn(updated);

        String body = objectMapper.writeValueAsString(new UpdateOrderStateRequest(OrderState.SHIPPED));

        mockMvc.perform(put("/orders/7/state")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7L))
                .andExpect(jsonPath("$.state").value("SHIPPED"));
    }

    @Test
    void updateState_invalidRequest_returns400() throws Exception {
        // newState is required (@NotNull on record), so null should fail validation
        mockMvc.perform(put("/orders/7/state")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void cancel_returns200() throws Exception {
        Order cancelled = Order.builder().id(9L).state(OrderState.CANCELLED).build();
        BDDMockito.given(orderService.cancelOrder(9L)).willReturn(cancelled);

        mockMvc.perform(post("/orders/9/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(9L))
                .andExpect(jsonPath("$.state").value("CANCELLED"));
    }

    @Test
    void history_returns200AndList() throws Exception {
        OrderStateHistory h1 = OrderStateHistory.builder()
                .id(1L)
                .entityId(10L)
                .previousState(OrderState.PENDING_PAYMENT)
                .newState(OrderState.PAID)
                .timestamp(Instant.parse("2024-01-01T00:00:00Z"))
                .build();

        OrderStateHistory h2 = OrderStateHistory.builder()
                .id(2L)
                .entityId(10L)
                .previousState(OrderState.PAID)
                .newState(OrderState.PROCESSING_IN_WAREHOUSE)
                .timestamp(Instant.parse("2024-01-02T00:00:00Z"))
                .build();

        BDDMockito.given(historyRepository.findByEntityIdOrderByTimestampAsc(10L)).willReturn(List.of(h1, h2));

        mockMvc.perform(get("/orders/10/history"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].entityId").value(10L))
                .andExpect(jsonPath("$[0].previousState").value("PENDING_PAYMENT"))
                .andExpect(jsonPath("$[0].newState").value("PAID"))
                .andExpect(jsonPath("$[1].newState").value("PROCESSING_IN_WAREHOUSE"));

        verify(historyRepository).findByEntityIdOrderByTimestampAsc(10L);
    }
}
