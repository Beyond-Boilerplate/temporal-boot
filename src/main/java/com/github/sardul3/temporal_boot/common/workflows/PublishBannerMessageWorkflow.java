package com.github.sardul3.temporal_boot.common.workflows;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import io.temporal.workflow.QueryMethod;

@WorkflowInterface
public interface PublishBannerMessageWorkflow {
    
    @WorkflowMethod
    String createAndPublishBannerMessage(String message);

    @QueryMethod
    String getWorkflowStatus();
}
