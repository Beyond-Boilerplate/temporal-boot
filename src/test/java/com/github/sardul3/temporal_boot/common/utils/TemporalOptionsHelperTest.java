package com.github.sardul3.temporal_boot.common.utils;

import com.github.sardul3.temporal_boot.common.config.TemporalConfigProperties;
import io.temporal.activity.ActivityOptions;
import io.temporal.client.WorkflowOptions;
import io.temporal.common.RetryOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TemporalOptionsHelperTest {

    @Mock
    private TemporalConfigProperties configMock;

    private TemporalOptionsHelper temporalOptionsHelper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        temporalOptionsHelper = new TemporalOptionsHelper(configMock);
    }

    @Test
    void testCreateActivityOptions_withValidConfig() {
        // Mock activity version config
        TemporalConfigProperties.RetryConfig retryConfig = new TemporalConfigProperties.RetryConfig();
        retryConfig.setInitialInterval(Duration.ofSeconds(2));
        retryConfig.setMaxAttempts(5);
        retryConfig.setBackoffCoefficient(1.5);

        TemporalConfigProperties.ActivityVersionConfig versionConfig = new TemporalConfigProperties.ActivityVersionConfig();
        versionConfig.setRetry(retryConfig);
        versionConfig.setScheduleToCloseTimeout(Duration.ofMinutes(3));
        versionConfig.setStartToCloseTimeout(Duration.ofMinutes(2));

        TemporalConfigProperties.ActivityConfig activityConfig = new TemporalConfigProperties.ActivityConfig();
        Map<String, TemporalConfigProperties.ActivityVersionConfig> versions = new HashMap<>();
        versions.put("v1", versionConfig);
        activityConfig.setVersions(versions);

        // Mock the config to return the mocked activity config
        when(configMock.getActivities()).thenReturn(Map.of("testActivity", activityConfig));

        // Call method under test
        ActivityOptions activityOptions = temporalOptionsHelper.createActivityOptions("testActivity", "v1");

        // Assertions
        assertNotNull(activityOptions);
        assertEquals(Duration.ofMinutes(3), activityOptions.getScheduleToCloseTimeout());
        assertEquals(Duration.ofMinutes(2), activityOptions.getStartToCloseTimeout());

        RetryOptions retryOptions = activityOptions.getRetryOptions();
        assertNotNull(retryOptions);
        assertEquals(Duration.ofSeconds(2), retryOptions.getInitialInterval());
        assertEquals(5, retryOptions.getMaximumAttempts());
        assertEquals(1.5, retryOptions.getBackoffCoefficient());
    }

    @Test
    void testCreateActivityOptions_withMissingConfig_shouldUseDefaults() {
        // Mock no config found
        when(configMock.getActivities()).thenReturn(Map.of());

        // Call method under test
        ActivityOptions activityOptions = temporalOptionsHelper.createActivityOptions("nonExistingActivity", "v1");

        // Assertions for default values
        assertNotNull(activityOptions);
        assertEquals(Duration.ofMinutes(5), activityOptions.getScheduleToCloseTimeout());
        assertEquals(Duration.ofMinutes(5), activityOptions.getStartToCloseTimeout());

        RetryOptions retryOptions = activityOptions.getRetryOptions();
        assertNotNull(retryOptions);
        assertEquals(Duration.ofSeconds(1), retryOptions.getInitialInterval());
        assertEquals(3, retryOptions.getMaximumAttempts());
        assertEquals(2.0, retryOptions.getBackoffCoefficient());
    }

    @Test
    void testCreateWorkflowOptions_withValidConfig() {
        // Mock workflow version config
        TemporalConfigProperties.WorkflowVersionConfig versionConfig = new TemporalConfigProperties.WorkflowVersionConfig();
        versionConfig.setExecutionTimeout(Duration.ofMinutes(30));
        versionConfig.setRunTimeout(Duration.ofMinutes(20));
        versionConfig.setTaskQueue("CUSTOM_TASK_QUEUE");

        TemporalConfigProperties.WorkflowConfig workflowConfig = new TemporalConfigProperties.WorkflowConfig();
        Map<String, TemporalConfigProperties.WorkflowVersionConfig> versions = new HashMap<>();
        versions.put("v1", versionConfig);
        workflowConfig.setVersions(versions);

        // Mock the config to return the mocked workflow config
        when(configMock.getWorkflows()).thenReturn(Map.of("testWorkflow", workflowConfig));

        // Call method under test
        WorkflowOptions workflowOptions = temporalOptionsHelper.createWorkflowOptions("testWorkflow", "v1");

        // Assertions
        assertNotNull(workflowOptions);
        assertEquals("CUSTOM_TASK_QUEUE", workflowOptions.getTaskQueue());
        assertEquals(Duration.ofMinutes(30), workflowOptions.getWorkflowExecutionTimeout());
        assertEquals(Duration.ofMinutes(20), workflowOptions.getWorkflowRunTimeout());
    }

    @Test
    void testCreateWorkflowOptions_withMissingConfig_shouldUseDefaults() {
        // Mock no config found
        when(configMock.getWorkflows()).thenReturn(Map.of());

        // Call method under test
        WorkflowOptions workflowOptions = temporalOptionsHelper.createWorkflowOptions("nonExistingWorkflow", "v1");

        // Assertions for default values
        assertNotNull(workflowOptions);
        assertEquals("DEFAULT_TASK_QUEUE", workflowOptions.getTaskQueue());
        assertEquals(Duration.ofHours(1), workflowOptions.getWorkflowExecutionTimeout());
        assertEquals(Duration.ofHours(1), workflowOptions.getWorkflowRunTimeout());
    }

    @Test
    void testCreateActivityOptions_withMissingVersion_shouldFallbackToDefaultVersion() {
        // Mock activity config but with missing version
        TemporalConfigProperties.ActivityConfig activityConfig = new TemporalConfigProperties.ActivityConfig();
        activityConfig.setVersions(new HashMap<>()); // Empty versions map

        when(configMock.getActivities()).thenReturn(Map.of("testActivity", activityConfig));

        // Call method under test
        ActivityOptions activityOptions = temporalOptionsHelper.createActivityOptions("testActivity", "nonExistingVersion");

        // Assertions for default fallback
        assertNotNull(activityOptions);
        assertEquals(Duration.ofMinutes(5), activityOptions.getScheduleToCloseTimeout());
        assertEquals(Duration.ofMinutes(5), activityOptions.getStartToCloseTimeout());

        RetryOptions retryOptions = activityOptions.getRetryOptions();
        assertNotNull(retryOptions);
        assertEquals(Duration.ofSeconds(1), retryOptions.getInitialInterval());
        assertEquals(3, retryOptions.getMaximumAttempts());
        assertEquals(2.0, retryOptions.getBackoffCoefficient());
    }
}

