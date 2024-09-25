package com.github.sardul3.temporal_boot.common.utils;

import com.github.sardul3.temporal_boot.common.config.TemporalConfigProperties;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.client.WorkflowOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Component
public class TemporalOptionsHelper {

    // Default constants to avoid magic values
    private static final Duration DEFAULT_INITIAL_RETRY_INTERVAL = Duration.ofSeconds(1);
    private static final int DEFAULT_MAX_ATTEMPTS = 3;
    private static final double DEFAULT_BACKOFF_COEFFICIENT = 2.0;
    private static final Duration DEFAULT_SCHEDULE_TO_CLOSE_TIMEOUT = Duration.ofMinutes(5);
    private static final Duration DEFAULT_START_TO_CLOSE_TIMEOUT = Duration.ofMinutes(5);
    private static final Duration DEFAULT_WORKFLOW_EXECUTION_TIMEOUT = Duration.ofHours(1);
    private static final Duration DEFAULT_WORKFLOW_RUN_TIMEOUT = Duration.ofHours(1);
    private static final String DEFAULT_TASK_QUEUE = "DEFAULT_TASK_QUEUE";

    private final TemporalConfigProperties config;

    public TemporalOptionsHelper(TemporalConfigProperties config) {
        this.config = config;
    }

    /**
     * Create ActivityOptions with fallback mechanisms and default values.
     */
    public ActivityOptions createActivityOptions(String activityName, String version) {
        TemporalConfigProperties.ActivityConfig activityConfig = config.getActivities().get(activityName);

        // Fallback if activity config or version is missing
        if (activityConfig == null) {
            log.warn("Activity configuration for {} not found, using default options.", activityName);
            return getDefaultActivityOptions();
        }

        TemporalConfigProperties.ActivityVersionConfig versionConfig = Optional.ofNullable(activityConfig.getVersions().get(version))
                .orElseGet(() -> {
                    log.warn("Version {} for activity {} not found, using default version.", version, activityName);
                    return getDefaultActivityVersionConfig();
                });

        // Retry options with fallback
        RetryOptions retryOptions = RetryOptions.newBuilder()
                .setInitialInterval(Optional.ofNullable(versionConfig.getRetry())
                        .map(TemporalConfigProperties.RetryConfig::getInitialInterval)
                        .orElse(DEFAULT_INITIAL_RETRY_INTERVAL)) // Default initial interval
                .setMaximumAttempts(Optional.ofNullable(versionConfig.getRetry())
                        .map(TemporalConfigProperties.RetryConfig::getMaxAttempts)
                        .orElse(DEFAULT_MAX_ATTEMPTS)) // Default max attempts
                .setBackoffCoefficient(Optional.ofNullable(versionConfig.getRetry())
                        .map(TemporalConfigProperties.RetryConfig::getBackoffCoefficient)
                        .orElse(DEFAULT_BACKOFF_COEFFICIENT)) // Default backoff coefficient
                .build();

        // Activity options with fallbacks for timeouts
        return ActivityOptions.newBuilder()
                .setScheduleToCloseTimeout(Optional.ofNullable(versionConfig.getScheduleToCloseTimeout())
                        .orElse(DEFAULT_SCHEDULE_TO_CLOSE_TIMEOUT)) // Default schedule-to-close timeout
                .setStartToCloseTimeout(Optional.ofNullable(versionConfig.getStartToCloseTimeout())
                        .orElse(DEFAULT_START_TO_CLOSE_TIMEOUT)) // Default start-to-close timeout
                .setRetryOptions(retryOptions)
                .build();
    }

    /**
     * Create WorkflowOptions with fallback mechanisms and default values.
     */
    public WorkflowOptions createWorkflowOptions(String workflowName, String version) {
        TemporalConfigProperties.WorkflowConfig workflowConfig = config.getWorkflows().get(workflowName);

        // Fallback if workflow config or version is missing
        if (workflowConfig == null) {
            log.warn("Workflow configuration for {} not found, using default options.", workflowName);
            return getDefaultWorkflowOptions();
        }

        TemporalConfigProperties.WorkflowVersionConfig versionConfig = Optional.ofNullable(workflowConfig.getVersions().get(version))
                .orElseGet(() -> {
                    log.warn("Version {} for workflow {} not found, using default version.", version, workflowName);
                    return getDefaultWorkflowVersionConfig();
                });

        // Workflow options with fallbacks for task queue and timeouts
        return WorkflowOptions.newBuilder()
                .setTaskQueue(Optional.ofNullable(versionConfig.getTaskQueue())
                        .orElse(DEFAULT_TASK_QUEUE)) // Default task queue
                .setWorkflowExecutionTimeout(Optional.ofNullable(versionConfig.getExecutionTimeout())
                        .orElse(DEFAULT_WORKFLOW_EXECUTION_TIMEOUT)) // Default workflow execution timeout
                .setWorkflowRunTimeout(Optional.ofNullable(versionConfig.getRunTimeout())
                        .orElse(DEFAULT_WORKFLOW_RUN_TIMEOUT)) // Default workflow run timeout
                .build();
    }

    /**
     * Provide default ActivityOptions if no configuration is found.
     */
    private ActivityOptions getDefaultActivityOptions() {
        log.info("Using default ActivityOptions as fallback.");
        return ActivityOptions.newBuilder()
                .setScheduleToCloseTimeout(DEFAULT_SCHEDULE_TO_CLOSE_TIMEOUT) // Default schedule-to-close timeout
                .setStartToCloseTimeout(DEFAULT_START_TO_CLOSE_TIMEOUT) // Default start-to-close timeout
                .setRetryOptions(RetryOptions.newBuilder()
                        .setInitialInterval(DEFAULT_INITIAL_RETRY_INTERVAL) // Default initial retry interval
                        .setMaximumAttempts(DEFAULT_MAX_ATTEMPTS) // Default max retry attempts
                        .setBackoffCoefficient(DEFAULT_BACKOFF_COEFFICIENT) // Default backoff coefficient
                        .build())
                .build();
    }

    /**
     * Provide default WorkflowOptions if no configuration is found.
     */
    private WorkflowOptions getDefaultWorkflowOptions() {
        log.info("Using default WorkflowOptions as fallback.");
        return WorkflowOptions.newBuilder()
                .setTaskQueue(DEFAULT_TASK_QUEUE) // Default task queue
                .setWorkflowExecutionTimeout(DEFAULT_WORKFLOW_EXECUTION_TIMEOUT) // Default workflow execution timeout
                .setWorkflowRunTimeout(DEFAULT_WORKFLOW_RUN_TIMEOUT) // Default workflow run timeout
                .build();
    }

    /**
     * Provide a default ActivityVersionConfig as a fallback when version information is missing.
     */
    private TemporalConfigProperties.ActivityVersionConfig getDefaultActivityVersionConfig() {
        log.info("Using default ActivityVersionConfig as fallback.");
        TemporalConfigProperties.ActivityVersionConfig defaultConfig = new TemporalConfigProperties.ActivityVersionConfig();
        defaultConfig.setScheduleToCloseTimeout(DEFAULT_SCHEDULE_TO_CLOSE_TIMEOUT); // Default schedule-to-close timeout
        defaultConfig.setStartToCloseTimeout(DEFAULT_START_TO_CLOSE_TIMEOUT); // Default start-to-close timeout
        defaultConfig.setRetry(new TemporalConfigProperties.RetryConfig()); // Default retry options
        defaultConfig.getRetry().setInitialInterval(DEFAULT_INITIAL_RETRY_INTERVAL);
        defaultConfig.getRetry().setMaxAttempts(DEFAULT_MAX_ATTEMPTS);
        defaultConfig.getRetry().setBackoffCoefficient(DEFAULT_BACKOFF_COEFFICIENT);
        return defaultConfig;
    }

    /**
     * Provide a default WorkflowVersionConfig as a fallback when version information is missing.
     */
    private TemporalConfigProperties.WorkflowVersionConfig getDefaultWorkflowVersionConfig() {
        log.info("Using default WorkflowVersionConfig as fallback.");
        TemporalConfigProperties.WorkflowVersionConfig defaultConfig = new TemporalConfigProperties.WorkflowVersionConfig();
        defaultConfig.setExecutionTimeout(DEFAULT_WORKFLOW_EXECUTION_TIMEOUT); // Default execution timeout
        defaultConfig.setRunTimeout(DEFAULT_WORKFLOW_RUN_TIMEOUT); // Default run timeout
        defaultConfig.setTaskQueue(DEFAULT_TASK_QUEUE); // Default task queue
        return defaultConfig;
    }
}
