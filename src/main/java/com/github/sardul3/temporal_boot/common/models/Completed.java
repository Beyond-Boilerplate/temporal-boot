package com.github.sardul3.temporal_boot.common.models;

public record Completed(String message) implements WorkflowStatus {
    @Override public boolean isFinal() { return true; }
}