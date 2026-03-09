package com.articurated.orderflow.background_jobs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class InvoiceJobServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void generateAndEmailInvoice_createsPdfFileWithDummyContent() throws Exception {
        InvoiceJobService service = new InvoiceJobService();
        ReflectionTestUtils.setField(service, "invoiceOutputDir", tempDir.toString());

        service.generateAndEmailInvoice(123L);

        Path pdf = tempDir.resolve("invoice-order-123.pdf");
        assertThat(Files.exists(pdf)).isTrue();

        String content = Files.readString(pdf);
        assertThat(content).contains("%PDF-1.4");
        assertThat(content).contains("Dummy Invoice for Order 123");
        assertThat(content).contains("%%EOF");
    }
}

