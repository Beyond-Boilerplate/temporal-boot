package com.github.sardul3.temporal_boot.common.models;

// Sealed interface for defining possible workflow statuses
public sealed interface WorkflowStatus permits Initialized, Validating, Scheduled, PaymentProcessing, Completed, Cancelled {

    // Common methods for all statuses, if needed
    String message();
    boolean isFinal();

    // Factory method to handle status transitions more easily
    static WorkflowStatus from(String statusMessage) {
        return switch (statusMessage) {
            case "VALIDATING" -> new Validating("Validating payment amount");
            case "SCHEDULED" -> new Scheduled("Payment task scheduled");
            case "PROCESSING" -> new PaymentProcessing("Payment is processing");
            case "COMPLETED" -> new Completed("Payment completed");
            case "CANCELLED" -> new Cancelled("Payment was cancelled");
            default -> new Initialized("Initialized workflow");
        };
    }
}


