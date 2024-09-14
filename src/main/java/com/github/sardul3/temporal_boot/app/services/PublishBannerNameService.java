package com.github.sardul3.temporal_boot.app.services;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.github.sardul3.temporal_boot.api.dtos.BannerNameRequest;
import com.github.sardul3.temporal_boot.api.dtos.BannerNameResponse;
import com.github.sardul3.temporal_boot.common.config.TemporalTaskQueues;
import com.github.sardul3.temporal_boot.common.workflows.PublishBannerMessageWorkflow;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PublishBannerNameService {

    private final String WORKFLOW_ID_PREFIX = "PBN-";
    
    private WorkflowClient workflowClient;

        public BannerNameResponse buildAndStartWorkflow(BannerNameRequest request, String correlationId) {
        String workflowId = computeWorkflowId(correlationId);
        PublishBannerMessageWorkflow workflow = buildWorkflow(workflowId);
        WorkflowClient.start(workflow::createAndPublishBannerMessage, request.getMessage());
        return buildResponse(workflowId);
    }

    private PublishBannerMessageWorkflow buildWorkflow(String workflowId) {
        return workflowClient.newWorkflowStub(PublishBannerMessageWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setTaskQueue(TemporalTaskQueues.BANNER_MESSAGE_SUBMISSION_QUEUE)
                        .setWorkflowId(WORKFLOW_ID_PREFIX + workflowId)
                        .build());
    }    

    private String computeWorkflowId(String correlationId) {
        if(correlationId != null && !correlationId.isBlank()) {
            return correlationId;
        }
        return UUID.randomUUID().toString();
    }

    private BannerNameResponse buildResponse(String workflowId) {
        BannerNameResponse res = new BannerNameResponse();
        res.setRequestTrackingId(workflowId);
        return res;
    }}
