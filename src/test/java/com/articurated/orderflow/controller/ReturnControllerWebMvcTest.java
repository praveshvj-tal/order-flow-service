package com.articurated.orderflow.controller;

import com.articurated.orderflow.entity.Order;
import com.articurated.orderflow.entity.ReturnRequest;
import com.articurated.orderflow.entity.ReturnState;
import com.articurated.orderflow.entity.ReturnStateHistory;
import com.articurated.orderflow.repository.ReturnStateHistoryRepository;
import com.articurated.orderflow.service.ReturnService;
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

@WebMvcTest(ReturnController.class)
class ReturnControllerWebMvcTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ReturnService returnService;

    @MockBean
    ReturnStateHistoryRepository historyRepository;

    @Test
    void initiateReturn_returns201() throws Exception {
        ReturnRequest created = ReturnRequest.builder()
                .id(1L)
                .order(Order.builder().id(99L).build())
                .state(ReturnState.REQUESTED)
                .build();
        BDDMockito.given(returnService.initiateReturn(99L)).willReturn(created);

        mockMvc.perform(post("/returns").queryParam("orderId", "99"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.orderId").value(99L))
                .andExpect(jsonPath("$.state").value("REQUESTED"));
    }

    @Test
    void getReturn_returns200() throws Exception {
        ReturnRequest rr = ReturnRequest.builder()
                .id(2L)
                .order(Order.builder().id(50L).build())
                .state(ReturnState.APPROVED)
                .build();
        BDDMockito.given(returnService.getReturnRequest(2L)).willReturn(rr);

        mockMvc.perform(get("/returns/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.state").value("APPROVED"));
    }

    @Test
    void approve_callsUpdateStateApproved() throws Exception {
        ReturnRequest rr = ReturnRequest.builder()
                .id(3L)
                .order(Order.builder().id(1L).build())
                .state(ReturnState.APPROVED)
                .build();
        BDDMockito.given(returnService.updateReturnState(3L, ReturnState.APPROVED)).willReturn(rr);

        mockMvc.perform(post("/returns/3/approve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("APPROVED"));

        verify(returnService).updateReturnState(3L, ReturnState.APPROVED);
    }

    @Test
    void reject_callsUpdateStateRejected() throws Exception {
        ReturnRequest rr = ReturnRequest.builder()
                .id(4L)
                .order(Order.builder().id(1L).build())
                .state(ReturnState.REJECTED)
                .build();
        BDDMockito.given(returnService.updateReturnState(4L, ReturnState.REJECTED)).willReturn(rr);

        mockMvc.perform(post("/returns/4/reject"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("REJECTED"));

        verify(returnService).updateReturnState(4L, ReturnState.REJECTED);
    }

    @Test
    void inTransit_callsUpdateStateInTransit() throws Exception {
        ReturnRequest rr = ReturnRequest.builder()
                .id(5L)
                .order(Order.builder().id(1L).build())
                .state(ReturnState.IN_TRANSIT)
                .build();
        BDDMockito.given(returnService.updateReturnState(5L, ReturnState.IN_TRANSIT)).willReturn(rr);

        mockMvc.perform(post("/returns/5/in-transit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("IN_TRANSIT"));

        verify(returnService).updateReturnState(5L, ReturnState.IN_TRANSIT);
    }

    @Test
    void received_callsUpdateStateReceived() throws Exception {
        ReturnRequest rr = ReturnRequest.builder()
                .id(6L)
                .order(Order.builder().id(1L).build())
                .state(ReturnState.RECEIVED)
                .build();
        BDDMockito.given(returnService.updateReturnState(6L, ReturnState.RECEIVED)).willReturn(rr);

        mockMvc.perform(post("/returns/6/received"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("RECEIVED"));

        verify(returnService).updateReturnState(6L, ReturnState.RECEIVED);
    }

    @Test
    void complete_callsUpdateStateCompleted() throws Exception {
        ReturnRequest rr = ReturnRequest.builder()
                .id(7L)
                .order(Order.builder().id(1L).build())
                .state(ReturnState.COMPLETED)
                .build();
        BDDMockito.given(returnService.updateReturnState(7L, ReturnState.COMPLETED)).willReturn(rr);

        mockMvc.perform(post("/returns/7/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("COMPLETED"));

        verify(returnService).updateReturnState(7L, ReturnState.COMPLETED);
    }

    @Test
    void history_returns200AndList() throws Exception {
        ReturnStateHistory h1 = ReturnStateHistory.builder()
                .id(1L)
                .entityId(10L)
                .previousState(ReturnState.REQUESTED)
                .newState(ReturnState.APPROVED)
                .timestamp(Instant.parse("2024-01-01T00:00:00Z"))
                .build();

        BDDMockito.given(historyRepository.findByEntityIdOrderByTimestampAsc(10L)).willReturn(List.of(h1));

        mockMvc.perform(get("/returns/10/history"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].entityId").value(10L))
                .andExpect(jsonPath("$[0].previousState").value("REQUESTED"))
                .andExpect(jsonPath("$[0].newState").value("APPROVED"));

        verify(historyRepository).findByEntityIdOrderByTimestampAsc(10L);
    }
}
