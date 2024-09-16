package com.github.sardul3.temporal_boot.common.workflows;

import com.github.sardul3.temporal_boot.api.dtos.ApprovalSignal;
import com.github.sardul3.temporal_boot.api.dtos.BannerChangeRequest;

import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface PublishBannerMessageWorkflow {
    
    @WorkflowMethod
    String createAndPublishBannerMessage(BannerChangeRequest request, String correlationId);

    @SignalMethod
    void signalApproval(ApprovalSignal approvalSignal);
}
