package com.github.sardul3.temporal_boot.common.workflows;

import com.github.sardul3.temporal_boot.common.models.Transaction;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

import java.time.LocalDateTime;

@WorkflowInterface
public interface SchedulePaymentWorkflow {

    @WorkflowMethod
    Transaction schedulePayment(String from, String to, double amount, LocalDateTime scheduledDate);

    @SignalMethod
    void fastForwardSignal(String scheduleId);
}
