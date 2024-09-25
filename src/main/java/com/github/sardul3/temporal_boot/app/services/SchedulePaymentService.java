package com.github.sardul3.temporal_boot.app.services;

import com.github.sardul3.temporal_boot.api.dtos.SchedulePaymentRequest;
import com.github.sardul3.temporal_boot.api.dtos.ScheduledPaymentConfirmation;
import com.github.sardul3.temporal_boot.common.config.TemporalConfigProperties;
import com.github.sardul3.temporal_boot.common.models.PaymentSchedulePayload;
import com.github.sardul3.temporal_boot.common.utils.TemporalConstants;
import com.github.sardul3.temporal_boot.common.utils.TemporalOptionsHelper;
import com.github.sardul3.temporal_boot.common.utils.WorkflowIdGenerator;
import com.github.sardul3.temporal_boot.common.workflows.SchedulePaymentWorkflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.api.enums.v1.WorkflowIdReusePolicy;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.common.RetryOptions;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
@AllArgsConstructor
public class SchedulePaymentService {

    private final String WORKFLOW_ID_PREFIX = "SPY-";

    private final WorkflowClient workflowClient;
    private final TemporalConfigProperties workflowConfig;
    private final TemporalOptionsHelper temporalOptionsHelper;

    public ScheduledPaymentConfirmation buildAndStartWorkflow(SchedulePaymentRequest request) {
        String workflowId = computeWorkflowId();
        LocalDateTime scheduledDateTime = convertDateTimeForSchedule(request.getWhen());
        SchedulePaymentWorkflow workflow = buildWorkflow(workflowId);

        WorkflowClient.start(workflow::schedulePayment, new PaymentSchedulePayload(request.getFrom(), request.getTo(), request.getAmount(), scheduledDateTime));
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
        return workflowClient.newWorkflowStub(SchedulePaymentWorkflow.class, createWorkflowOptions(workflowId));
    }

    String computeWorkflowId() {
        return  WorkflowIdGenerator.generateWorkflowId(TemporalConstants.Workflows.PAYMENT_SCHEDULING_WORKFLOW, workflowConfig);
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

    ScheduledPaymentConfirmation buildConfirmation(String workflowId) {
        ScheduledPaymentConfirmation confirmation = new ScheduledPaymentConfirmation();
        confirmation.setPaymentScheduleId(workflowId);
        return confirmation;
    }

    // Refactor to dynamically retrieve workflow options using TemporalOptionsHelper
    WorkflowOptions createWorkflowOptions(String workflowId) {
        // Get the current version of the PAYMENT_SCHEDULING_WORKFLOW
        String currentVersion = workflowConfig.getCurrentVersions().getWorkflows().get(TemporalConstants.Workflows.PAYMENT_SCHEDULING_WORKFLOW);

        // Use TemporalOptionsHelper to dynamically get the workflow options
        WorkflowOptions workflowOptions = temporalOptionsHelper.createWorkflowOptions(TemporalConstants.Workflows.PAYMENT_SCHEDULING_WORKFLOW, currentVersion)
                .toBuilder() // Ensure we can modify the options further
                .setWorkflowId(workflowId) // Set the dynamically generated workflow ID
                .setWorkflowIdReusePolicy(WorkflowIdReusePolicy.WORKFLOW_ID_REUSE_POLICY_ALLOW_DUPLICATE_FAILED_ONLY)
                .build();

        return workflowOptions;
    }
}
