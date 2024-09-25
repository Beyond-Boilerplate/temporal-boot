package com.github.sardul3.temporal_boot.common.models;

// Various status implementations
public record Initialized(String message) implements WorkflowStatus {
    @Override public boolean isFinal() { return false; }
}