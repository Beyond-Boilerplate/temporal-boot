package com.github.sardul3.temporal_boot.common.models;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionResult {
    private String transactionId;
    private boolean success;
    private String message;
    private LocalDateTime processedAt;
    private Transaction transaction;
}
