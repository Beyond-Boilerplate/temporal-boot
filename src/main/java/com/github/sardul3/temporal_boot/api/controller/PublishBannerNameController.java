package com.github.sardul3.temporal_boot.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.sardul3.temporal_boot.api.dtos.ApprovalSignal;
import com.github.sardul3.temporal_boot.api.dtos.BannerChangeRequest;
import com.github.sardul3.temporal_boot.api.dtos.BannerNameRequest;
import com.github.sardul3.temporal_boot.api.dtos.BannerNameResponse;
import com.github.sardul3.temporal_boot.app.services.PublishBannerNameService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/banner-change")
@AllArgsConstructor
public class PublishBannerNameController {
    
    private final PublishBannerNameService publishBannerNameService;

    @PostMapping
    public ResponseEntity<BannerNameResponse> submitNewBannerNameRequest(
        @RequestBody BannerChangeRequest request,
        @RequestHeader(value = "X-Correlation-ID", required = false) String xCoorrelationId
    ) {
        BannerNameResponse response = publishBannerNameService.buildAndStartWorkflow(request, xCoorrelationId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{workflowId}/approve")
    public ResponseEntity<String> signalApproval(
        @PathVariable String workflowId,
        @RequestBody ApprovalSignal approvalSignal) {
            publishBannerNameService.signalApproval(approvalSignal, workflowId);
            return ResponseEntity.ok("Approval signal sent for workflow ID: " + workflowId);
    }
}
