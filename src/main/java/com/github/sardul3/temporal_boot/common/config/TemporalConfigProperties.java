package com.github.sardul3.temporal_boot.common.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.List;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "temporal-config")
public class TemporalConfigProperties {
 
    private String server;
    private String namespace;
    private Map<String, WorkflowConfig> workflows;
    private Map<String, ActivityConfig> activities;
    private Map<String, WorkerConfig> workers;
    private CurrentVersionsConfig currentVersions;

    @Setter
    @Getter
    public static class WorkflowConfig {
        private String prefix;
        private String keyGenerationStrategy;
        private Map<String, WorkflowVersionConfig> versions;
    }

    @Setter
    @Getter
    public static class WorkflowVersionConfig {
        private Duration executionTimeout;
        private Duration runTimeout;
        private String taskQueue;
    }

    @Setter
    @Getter
    public static class ActivityConfig {
        private Map<String, ActivityVersionConfig> versions;
    }

    @Setter
    @Getter
    public static class ActivityVersionConfig {
        private Duration scheduleToCloseTimeout;
        private Duration startToCloseTimeout;
        private RetryConfig retry;
    }

    @Setter
    @Getter
    public static class RetryConfig {
        private Duration initialInterval;
        private int maxAttempts;
        private double backoffCoefficient;
    }

    @Setter
    @Getter
    public static class WorkerConfig {
        private String taskQueue;
        private List<String> workflows;   // A list of workflows for the worker
        private List<String> activities;  // A list of activities for the worker
    }

    @Setter
    @Getter
    public static class CurrentVersionsConfig {
        private Map<String, String> workflows;
        private Map<String, String> activities;
        private Map<String, String> workers;
    }
}

