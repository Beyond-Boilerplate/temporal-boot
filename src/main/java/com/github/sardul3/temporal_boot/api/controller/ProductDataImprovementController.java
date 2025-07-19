package com.github.sardul3.temporal_boot.api.controller;

import com.github.sardul3.temporal_boot.api.dtos.AIWorkflowRequest;
import com.github.sardul3.temporal_boot.api.dtos.AIWorkflowResponse;
import com.github.sardul3.temporal_boot.app.services.GenericWorkflowService;
import com.github.sardul3.temporal_boot.common.workflows.ProductDataImprovementWorkflow;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product/improve")
@RequiredArgsConstructor
public class ProductDataImprovementController {

    private final GenericWorkflowService genericWorkflowService;

    @PostMapping
    public ResponseEntity<AIWorkflowResponse> runImprovement(
        @RequestBody AIWorkflowRequest request,
        @RequestHeader(name = "X-Correlation-ID", required = false) String correlationId
    ) {
        AIWorkflowResponse response = genericWorkflowService.startWorkflow(
            request, ProductDataImprovementWorkflow.class, correlationId
        );
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<String> getWorkflowStatus(@PathVariable String id) {
        String status = genericWorkflowService.queryWorkflowStatus(ProductDataImprovementWorkflow.class, id);
        return ResponseEntity.ok(status);
    }
}

