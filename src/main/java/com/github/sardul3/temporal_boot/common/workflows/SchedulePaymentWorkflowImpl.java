package com.github.sardul3.temporal_boot.common.workflows;

import com.github.sardul3.temporal_boot.app.exceptions.TransactionCancelledException;
import com.github.sardul3.temporal_boot.common.activities.SchedulePaymentActivities;
import com.github.sardul3.temporal_boot.common.models.Transaction;
import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;
import java.time.LocalDateTime;

public class SchedulePaymentWorkflowImpl implements SchedulePaymentWorkflow {

    private boolean isCancelled = false;

    private final SchedulePaymentActivities activities = Workflow.newActivityStub(
            SchedulePaymentActivities.class,
            ActivityOptions.newBuilder()
                    .setScheduleToCloseTimeout(Duration.ofMinutes(5)) // Max time allowed from schedule to activity completion
                    .build()
    );

    @Override
    public Transaction schedulePayment(String from, String to, double amount, LocalDateTime scheduledDate) {
        // Step 1: Validate the payment amount via an activity
        activities.validateAmount(amount);

        // Step 2: Create a scheduled task for logging or record purposes
        activities.createScheduledTask(from, to, amount, scheduledDate);

        // Step 3: Sleep until the scheduled time
        Duration delay = Duration.between(LocalDateTime.now(), scheduledDate);
        if (!delay.isNegative()) {
            Workflow.await(delay, () -> isCancelled);
//            Workflow.sleep(delay);
        }

        if(isCancelled) {
            throw new TransactionCancelledException();
        }

        // Step 4: Execute the payment once the scheduled time has arrived
        return activities.runSchedulePayment(from, to, amount, scheduledDate);
    }

    @Override
    public void cancelPayment() {
        this.isCancelled = true;
    }
}
