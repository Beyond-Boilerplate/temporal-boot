package com.github.sardul3.temporal_boot.common.workflows;

import com.github.sardul3.temporal_boot.common.activities.SchedulePaymentActivities;
import com.github.sardul3.temporal_boot.common.models.*;
import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

public class SchedulePaymentWorkflowImpl implements SchedulePaymentWorkflow {

    private boolean fastForward = false;
    private WorkflowStatus workflowStatus = new Initialized("Workflow started"); // Initial status

    private final SchedulePaymentActivities activities;
    private final SchedulePaymentActivities noRetryActivities;

    public SchedulePaymentWorkflowImpl(Map<String, ActivityOptions> activityOptionsMap) {
        // Use the map to retrieve ActivityOptions dynamically
        this.activities = Workflow.newActivityStub(SchedulePaymentActivities.class, 
                activityOptionsMap.get("schedulePaymentActivities"));

        this.noRetryActivities = Workflow.newActivityStub(SchedulePaymentActivities.class, 
                activityOptionsMap.get("schedulePaymentActivitiesNoRetry"));
    }

    @Override
    public Transaction schedulePayment(PaymentSchedulePayload payload) {
        // Step 1: Update status to Validating
        updateWorkflowStatus(new Validating("Validating payment amount..."));

        // Step 1: Validate the payment amount via an activity
        noRetryActivities.validateAmount(payload.getAmount());

        // Step 2: Update status to Scheduled
        updateWorkflowStatus(new Scheduled("Payment task scheduled..."));

        // Step 2: Create a scheduled task for logging or record purposes
        activities.createScheduledTask(payload.getFrom(), payload.getTo(), payload.getAmount(), payload.getScheduledDate());

        // Step 3: Sleep until the scheduled time 
        //ALERT: do not use system time such as LocalDateTime (side-effect)
        // Duration delay = Duration.between(LocalDateTime.now(), scheduledDate);

        long currentTimeMillis = Workflow.currentTimeMillis();  // Use workflow-provided time
        LocalDateTime workflowNow = LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTimeMillis), ZoneId.systemDefault());
        Duration delay = Duration.between(workflowNow, payload.getScheduledDate());

        if (!delay.isNegative()) {
            // blocking call (await or sleep)
            Workflow.await(delay, () -> fastForward);

            // non-blocking
            // Workflow.newTimer(delay);
            // Workflow.sleep(delay);
        }

        if (fastForward) {
            updateWorkflowStatus(new Cancelled("Payment fast-forwarded and cancelled."));
            Workflow.getLogger(SchedulePaymentWorkflowImpl.class).info("Payment was fast-forwarded to prevent scheduling.");
            return null;
        }

        // Step 4: Update status to PaymentProcessing
        updateWorkflowStatus(new PaymentProcessing("Processing scheduled payment..."));

        // Step 4: Execute the payment once the scheduled time has arrived
        Transaction transaction = activities.runSchedulePayment(payload.getFrom(), payload.getTo(), payload.getAmount(), payload.getScheduledDate());

        // Step 5: Update status to Completed
        updateWorkflowStatus(new Completed("Payment completed successfully."));
        return transaction;
    }

    @Override
    public void fastForwardSignal(String scheduleId) {
        this.fastForward = true;
    }

    // Helper method to update workflow status and log the changes
    private void updateWorkflowStatus(WorkflowStatus newStatus) {
        this.workflowStatus = newStatus;
        Workflow.getLogger(SchedulePaymentWorkflowImpl.class).info("Status updated: " + newStatus.message());
    }
}
