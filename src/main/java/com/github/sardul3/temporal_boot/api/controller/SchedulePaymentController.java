package com.github.sardul3.temporal_boot.api.controller;

import com.github.sardul3.temporal_boot.api.dtos.SchedulePaymentRequest;
import com.github.sardul3.temporal_boot.api.dtos.ScheduledPaymentConfirmation;
import com.github.sardul3.temporal_boot.app.services.SchedulePaymentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@AllArgsConstructor
public class SchedulePaymentController {

    private final SchedulePaymentService schedulePaymentService;

    @PostMapping("/schedule")
    public ResponseEntity<ScheduledPaymentConfirmation> submitScheduledPayment(
            @RequestBody SchedulePaymentRequest request,
            @RequestHeader(value = "X-Correlation-ID", required = false) String xCorrelationId
            ) {
        ScheduledPaymentConfirmation confirmation = schedulePaymentService.buildAndStartWorkflow(request, xCorrelationId);
        return ResponseEntity.status(HttpStatus.CREATED).body(confirmation);
    }

}
