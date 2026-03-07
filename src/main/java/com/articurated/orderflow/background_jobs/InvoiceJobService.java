package com.articurated.orderflow.background_jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceJobService {

    @Value("${orderflow.jobs.invoice.output-dir:./invoices}")
    private String invoiceOutputDir;

    @Async
    public void generateAndEmailInvoice(Long orderId) {
        try {
            Path dir = Path.of(invoiceOutputDir);
            Files.createDirectories(dir);

            Path pdf = dir.resolve("invoice-order-" + orderId + ".pdf");

            // Minimal "dummy PDF" content. Enough to create a valid file for verification.
            // Not aiming for full PDF spec completeness.
            String content = "%PDF-1.4\n% Dummy Invoice for Order " + orderId + "\n%%EOF\n";
            Files.writeString(pdf, content);

            log.info("Generated invoice PDF at {} for orderId={}", pdf.toAbsolutePath(), orderId);
            log.info("Simulating invoice email send for orderId={} (attachment={})", orderId, pdf.getFileName());
        } catch (IOException e) {
            log.error("Failed to generate invoice for orderId={}", orderId, e);
        }
    }
}

