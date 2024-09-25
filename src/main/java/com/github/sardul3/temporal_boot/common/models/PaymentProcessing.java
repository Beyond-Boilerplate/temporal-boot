package com.github.sardul3.temporal_boot.common.models;

public record PaymentProcessing(String message) implements WorkflowStatus {
    @Override public boolean isFinal() { return false; }
}