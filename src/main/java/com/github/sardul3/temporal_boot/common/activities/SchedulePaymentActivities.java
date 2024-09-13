package com.github.sardul3.temporal_boot.common.activities;

import com.github.sardul3.temporal_boot.common.models.Transaction;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import java.time.LocalDateTime;

@ActivityInterface
public interface SchedulePaymentActivities {

    @ActivityMethod
    void validateAmount(double amount);

    @ActivityMethod
    void createScheduledTask(String from, String to, double amount, LocalDateTime scheduledDate);

    @ActivityMethod
    Transaction runSchedulePayment(String from, String to, double amount, LocalDateTime scheduledDate);
}
