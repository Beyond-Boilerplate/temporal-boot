package com.github.sardul3.temporal_boot.app.services;

import com.github.sardul3.temporal_boot.api.dtos.SchedulePaymentRequest;
import com.github.sardul3.temporal_boot.api.dtos.ScheduledPaymentConfirmation;
import com.github.sardul3.temporal_boot.common.config.TemporalTaskQueues;
import com.github.sardul3.temporal_boot.common.workflows.SchedulePaymentWorkflow;
import io.temporal.api.enums.v1.WorkflowIdReusePolicy;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SchedulePaymentService {

    private final String WORKFLOW_ID_PREFIX = "SPY-";

    private final WorkflowClient workflowClient;

    public ScheduledPaymentConfirmation buildAndStartWorkflow(SchedulePaymentRequest request, String correlationId) {
        String workflowId = computeWorkflowId(correlationId);
        LocalDateTime scheduledDateTime = convertDateTimeForSchedule(request.getWhen());
        SchedulePaymentWorkflow workflow = buildWorkflow(workflowId);
        WorkflowClient.start(workflow::schedulePayment, request.getFrom(), request.getTo(), request.getAmount(), scheduledDateTime);
        return buildConfirmation(workflowId);
    }

    // Canceling a Workflow is a gentle request for the Workflow Execution to stop, but allows the process to perform cleanup before exiting
    // Terminating a Workflow Execution is more abrupt and is similar to killing a process
    public String buildAndStartWorkflowForCancellation(String scheduleId) {
        // USE Workflow.newExternalWorkflowStub to send SIGNALs from one workflow to another
        SchedulePaymentWorkflow workflow = 
            workflowClient.newWorkflowStub(SchedulePaymentWorkflow.class, WORKFLOW_ID_PREFIX + scheduleId);
        WorkflowStub workflowStub = WorkflowStub.fromTyped(workflow);
        workflowStub.cancel();
        return "Cancellation Requested";
    }

    public String buildAndStartWorkflowForFastForward(String workflowID) {
        SchedulePaymentWorkflow workflow = 
             workflowClient.newWorkflowStub(SchedulePaymentWorkflow.class, WORKFLOW_ID_PREFIX + workflowID);
        workflow.fastForwardSignal(workflowID);
        return "Fast Forward Requested";
    }

    private SchedulePaymentWorkflow buildWorkflow(String workflowId) {
        return workflowClient.newWorkflowStub(SchedulePaymentWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setTaskQueue(TemporalTaskQueues.PAYMENT_SCHEDULE_QUEUE)
                        .setWorkflowId(WORKFLOW_ID_PREFIX + workflowId)
                        .setWorkflowIdReusePolicy(
                                WorkflowIdReusePolicy.WORKFLOW_ID_REUSE_POLICY_ALLOW_DUPLICATE_FAILED_ONLY
                        )
                        .build());
    }

    private String computeWorkflowId(String correlationId) {
        if(correlationId != null && !correlationId.isBlank()) {
            return correlationId;
        }
        return UUID.randomUUID().toString();
    }

    private LocalDateTime convertDateTimeForSchedule(String datetime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime scheduledDateTime;

        try {
            scheduledDateTime = LocalDateTime.parse(datetime, formatter);
        } catch (DateTimeParseException e) {
            // If date format is incorrect, set the time to 30 seconds from now
            scheduledDateTime = LocalDateTime.now().plusSeconds(30);
        }
        return scheduledDateTime;
    }

    private ScheduledPaymentConfirmation buildConfirmation(String workflowId) {
        ScheduledPaymentConfirmation confirmation = new ScheduledPaymentConfirmation();
        confirmation.setPaymentScheduleId(workflowId);
        return confirmation;
    }
}
