package com.github.sardul3.temporal_boot.common.workflows;

import com.github.sardul3.temporal_boot.app.exceptions.TransactionCancelledException;
import com.github.sardul3.temporal_boot.common.activities.PublishBannerMessageActivities;
import com.github.sardul3.temporal_boot.common.activities.SchedulePaymentActivities;
import com.github.sardul3.temporal_boot.common.models.Transaction;

import io.temporal.activity.Activity;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class SchedulePaymentWorkflowImpl implements SchedulePaymentWorkflow {

    private boolean isCancelled = false;

    private final SchedulePaymentActivities activities = Workflow.newActivityStub(
            SchedulePaymentActivities.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofMinutes(5)) // Max time allowed from schedule to activity completion
                    .build()
    );

    private final ActivityOptions noRetryOptions = ActivityOptions.newBuilder()
        .setStartToCloseTimeout(Duration.ofMinutes(30))
        .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(1).build()) // No retry
        .build();

    SchedulePaymentActivities noRetryActivities = Workflow.newActivityStub(SchedulePaymentActivities.class, noRetryOptions);


    @Override
    public Transaction schedulePayment(String from, String to, double amount, LocalDateTime scheduledDate) {
        // Step 1: Validate the payment amount via an activity
        noRetryActivities.validateAmount(amount);

        // Step 2: Create a scheduled task for logging or record purposes
        activities.createScheduledTask(from, to, amount, scheduledDate);

        // Step 3: Sleep until the scheduled time 
        //ALERT: do not use system time such as LocalDateTime (side-effect)
        // Duration delay = Duration.between(LocalDateTime.now(), scheduledDate);

        long currentTimeMillis = Workflow.currentTimeMillis();  // Use workflow-provided time
        LocalDateTime workflowNow = LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTimeMillis), ZoneId.systemDefault());
        Duration delay = Duration.between(workflowNow, scheduledDate);

        if (!delay.isNegative()) {
            // blocking call (await or sleep)
            Workflow.await(delay, () -> isCancelled);

            // non-blocking
            Workflow.newTimer(delay);
//            Workflow.sleep(delay);
        }

        if(isCancelled) {
            throw Activity.wrap(new TransactionCancelledException());
        }

        // Step 4: Execute the payment once the scheduled time has arrived
        return activities.runSchedulePayment(from, to, amount, scheduledDate);
    }

    @Override
    public void cancelPayment(String scheduleId) {
        this.isCancelled = true;
    }
}
