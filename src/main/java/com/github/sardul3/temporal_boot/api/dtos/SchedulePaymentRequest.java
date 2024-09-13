package com.github.sardul3.temporal_boot.api.dtos;

import lombok.Data;

@Data
public class SchedulePaymentRequest {
    private String from;
    private String to;
    private double amount;
    private String when;
}
