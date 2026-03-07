package com.articurated.orderflow.background_jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefundJobService {

    @Value("${orderflow.jobs.refund.payment-gateway-base-url:http://localhost:8080}")
    private String paymentGatewayBaseUrl;

    private final RestClient restClient = RestClient.create();

    @Async
    public void processRefund(Long returnRequestId) {
        try {
            String url = paymentGatewayBaseUrl + "/mock-payment-gateway/refunds";
            RefundRequest payload = new RefundRequest(returnRequestId);

            RefundResponse response = restClient.post()
                    .uri(url)
                    .body(payload)
                    .retrieve()
                    .body(RefundResponse.class);

            log.info("Refund processed for returnRequestId={}, gatewayResponse={}", returnRequestId, response);
        } catch (Exception e) {
            log.error("Refund processing failed for returnRequestId={}", returnRequestId, e);
        }
    }

    public record RefundRequest(Long returnRequestId) {
    }

    public record RefundResponse(String status) {
    }
}

