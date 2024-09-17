package com.github.sardul3.temporal_boot.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.sardul3.temporal_boot.api.dtos.BannerNameRequest;
import com.github.sardul3.temporal_boot.api.dtos.BannerNameResponse;
import com.github.sardul3.temporal_boot.app.services.PublishBannerNameService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/banner/name")
@AllArgsConstructor
public class PublishBannerNameController {
    
    private final PublishBannerNameService publishBannerNameService;

    @PostMapping
    public ResponseEntity<BannerNameResponse> submitNewBannerNameRequest(
        @RequestBody BannerNameRequest request,
        @RequestHeader(required = false) String xCoorrelationId
    ) {
        BannerNameResponse response = publishBannerNameService.buildAndStartWorkflow(request, xCoorrelationId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/status")
    public String getStatusUpdate(@PathVariable(value = "id") String scheduleId) {
        String message = publishBannerNameService.buildAndStartWorkflowStatusQuery(scheduleId);
        return message;
    }
}
