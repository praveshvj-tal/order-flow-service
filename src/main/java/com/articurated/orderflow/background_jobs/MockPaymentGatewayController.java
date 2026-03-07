package com.articurated.orderflow.background_jobs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mock-payment-gateway")
public class MockPaymentGatewayController {

    @PostMapping("/refunds")
    public ResponseEntity<RefundJobService.RefundResponse> refund(@RequestBody RefundJobService.RefundRequest request) {
        // Mock success response as required: "make an API call to a mock payment gateway service"
        return ResponseEntity.ok(new RefundJobService.RefundResponse("SUCCESS"));
    }
}

