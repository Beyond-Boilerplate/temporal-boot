package com.github.sardul3.temporal_boot.common.models;

import io.temporal.activity.ActivityOptions;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentSchedulePayload {
    private String from;
    private String to;
    private double amount;
    private LocalDateTime scheduledDate;
}
