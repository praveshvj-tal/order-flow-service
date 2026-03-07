package com.articurated.orderflow.controller;

import com.articurated.orderflow.dto.ReturnRequestResponse;
import com.articurated.orderflow.dto.ReturnStateHistoryResponse;
import com.articurated.orderflow.entity.ReturnRequest;
import com.articurated.orderflow.entity.ReturnState;
import com.articurated.orderflow.mapper.ReturnRequestMapper;
import com.articurated.orderflow.repository.ReturnStateHistoryRepository;
import com.articurated.orderflow.service.ReturnService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/returns")
@RequiredArgsConstructor
public class ReturnController {

    private final ReturnService returnService;
    private final ReturnStateHistoryRepository historyRepository;

    @PostMapping
    public ResponseEntity<ReturnRequestResponse> initiate(@RequestParam Long orderId) {
        ReturnRequest created = returnService.initiateReturn(orderId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ReturnRequestMapper.toResponse(created));
    }

    @GetMapping("/{returnId}")
    public ResponseEntity<ReturnRequestResponse> get(@PathVariable Long returnId) {
        return ResponseEntity.ok(ReturnRequestMapper.toResponse(returnService.getReturnRequest(returnId)));
    }

    @PostMapping("/{returnId}/approve")
    public ResponseEntity<ReturnRequestResponse> approve(@PathVariable Long returnId) {
        return ResponseEntity.ok(ReturnRequestMapper.toResponse(returnService.updateReturnState(returnId, ReturnState.APPROVED)));
    }

    @PostMapping("/{returnId}/reject")
    public ResponseEntity<ReturnRequestResponse> reject(@PathVariable Long returnId) {
        return ResponseEntity.ok(ReturnRequestMapper.toResponse(returnService.updateReturnState(returnId, ReturnState.REJECTED)));
    }

    @PostMapping("/{returnId}/in-transit")
    public ResponseEntity<ReturnRequestResponse> inTransit(@PathVariable Long returnId) {
        return ResponseEntity.ok(ReturnRequestMapper.toResponse(returnService.updateReturnState(returnId, ReturnState.IN_TRANSIT)));
    }

    @PostMapping("/{returnId}/received")
    public ResponseEntity<ReturnRequestResponse> received(@PathVariable Long returnId) {
        return ResponseEntity.ok(ReturnRequestMapper.toResponse(returnService.updateReturnState(returnId, ReturnState.RECEIVED)));
    }

    @PostMapping("/{returnId}/complete")
    public ResponseEntity<ReturnRequestResponse> complete(@PathVariable Long returnId) {
        return ResponseEntity.ok(ReturnRequestMapper.toResponse(returnService.updateReturnState(returnId, ReturnState.COMPLETED)));
    }

    @GetMapping("/{returnId}/history")
    public ResponseEntity<List<ReturnStateHistoryResponse>> history(@PathVariable Long returnId) {
        return ResponseEntity.ok(historyRepository.findByEntityIdOrderByTimestampAsc(returnId).stream()
                .map(h -> ReturnStateHistoryResponse.builder()
                        .entityId(h.getEntityId())
                        .previousState(h.getPreviousState())
                        .newState(h.getNewState())
                        .timestamp(h.getTimestamp())
                        .build())
                .toList());
    }
}

