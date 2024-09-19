package com.github.sardul3.temporal_boot.common.workflows;

import com.github.sardul3.temporal_boot.common.models.PaymentSchedulePayload;
import com.github.sardul3.temporal_boot.common.models.Transaction;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

import java.time.LocalDateTime;

@WorkflowInterface
public interface SchedulePaymentWorkflow {

    @WorkflowMethod
    Transaction schedulePayment(PaymentSchedulePayload payload);

    @SignalMethod
    void fastForwardSignal(String scheduleId);
}
