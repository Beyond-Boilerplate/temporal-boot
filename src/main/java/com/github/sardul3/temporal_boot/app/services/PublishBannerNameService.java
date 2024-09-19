package com.github.sardul3.temporal_boot.app.services;

import org.springframework.stereotype.Service;

import com.github.sardul3.temporal_boot.api.dtos.BannerNameRequest;
import com.github.sardul3.temporal_boot.api.dtos.BannerNameResponse;
import com.github.sardul3.temporal_boot.common.config.TemporalConfigProperties;
import com.github.sardul3.temporal_boot.common.utils.TemporalConstants;
import com.github.sardul3.temporal_boot.common.utils.WorkflowIdGenerator;
import com.github.sardul3.temporal_boot.common.workflows.PublishBannerMessageWorkflow;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PublishBannerNameService {

    private final String WORKFLOW_ID_PREFIX = "PBN-";
    
    private WorkflowClient workflowClient;
    private final TemporalConfigProperties workflowConfig;


    public BannerNameResponse buildAndStartWorkflow(BannerNameRequest request) {
        // Decision :  Seperating correlationID and workflowID generation
        // Why      :  Can result in ALREADY_EXISTS error if same correlationID is sent
        String workflowId = computeWorkflowId();

        PublishBannerMessageWorkflow workflow = buildWorkflow(workflowId);
        WorkflowClient.start(workflow::createAndPublishBannerMessage, request.getMessage());
        return buildResponse(workflowId);
    }

    // USE workflowStub.cancel() to cancel the workflow execution
    public String buildAndStartWorkflowStatusQuery(String workflowId) {
        PublishBannerMessageWorkflow workflow = 
            workflowClient.newWorkflowStub(PublishBannerMessageWorkflow.class, WORKFLOW_ID_PREFIX + workflowId);
        return workflow.getWorkflowStatus();
    }

    private PublishBannerMessageWorkflow buildWorkflow(String workflowId) {
        String currentVersion = workflowConfig.getCurrentVersions().getWorkflows().get(TemporalConstants.Workflows.PUBLISH_BANNER_MESSAGE_WORKFLOW);
        String taskQueue = workflowConfig.getWorkflows()
                                    .get(TemporalConstants.Workflows.PUBLISH_BANNER_MESSAGE_WORKFLOW)
                                    .getVersions().get(currentVersion)
                                    .getTaskQueue();
        return workflowClient.newWorkflowStub(PublishBannerMessageWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setTaskQueue(taskQueue)
                        .setWorkflowId(workflowId)
                        .build());
    }    

    private String computeWorkflowId() {
        
        return  WorkflowIdGenerator.generateWorkflowId(TemporalConstants.Workflows.PUBLISH_BANNER_MESSAGE_WORKFLOW, workflowConfig);
    }

    private BannerNameResponse buildResponse(String workflowId) {
        BannerNameResponse res = new BannerNameResponse();
        res.setRequestTrackingId(workflowId);
        return res;
    }}
