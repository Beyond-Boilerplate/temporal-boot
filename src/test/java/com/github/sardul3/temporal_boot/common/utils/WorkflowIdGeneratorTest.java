package com.github.sardul3.temporal_boot.common.utils;

import com.github.sardul3.temporal_boot.common.config.TemporalConfigProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class WorkflowIdGeneratorTest {

    private TemporalConfigProperties configMock;
    private Map<String, TemporalConfigProperties.WorkflowConfig> workflowsMap;

    @BeforeEach
    void setUp() {
        configMock = Mockito.mock(TemporalConfigProperties.class);
        workflowsMap = new HashMap<>();
    }

    @Test
    void generateWorkflowId_validWorkflowNameAndUUIDStrategy() {
        // Arrange
        String workflowName = "TestWorkflow";
        TemporalConfigProperties.WorkflowConfig workflowConfig = new TemporalConfigProperties.WorkflowConfig();
        workflowConfig.setPrefix("TEST");
        workflowConfig.setKeyGenerationStrategy("UUID");

        workflowsMap.put(workflowName, workflowConfig);
        when(configMock.getWorkflows()).thenReturn(workflowsMap);

        // Act
        String workflowId = WorkflowIdGenerator.generateWorkflowId(workflowName, configMock);

        // Assert
        assertNotNull(workflowId);
        assertTrue(workflowId.startsWith("TEST-"));

        // Extract the UUID part after the prefix and validate it
        String[] parts = workflowId.split("TEST-");
        assertEquals(2, parts.length);  // Ensure the prefix and UUID are both present
        assertDoesNotThrow(() -> UUID.fromString(parts[1]));  // Validate UUID part   
 }

    @Test
    void generateWorkflowId_workflowNameNotFound() {
        // Arrange
        String workflowName = "InvalidWorkflow";
        when(configMock.getWorkflows()).thenReturn(workflowsMap);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            WorkflowIdGenerator.generateWorkflowId(workflowName, configMock));
        
        assertEquals("Workflow name not found: " + workflowName, exception.getMessage());
    }

    @Test
    void generateWorkflowId_invalidKeyGenerationStrategy() {
        // Arrange
        String workflowName = "TestWorkflow";
        TemporalConfigProperties.WorkflowConfig workflowConfig = new TemporalConfigProperties.WorkflowConfig();
        workflowConfig.setPrefix("TEST");
        workflowConfig.setKeyGenerationStrategy("INVALID_STRATEGY");

        workflowsMap.put(workflowName, workflowConfig);
        when(configMock.getWorkflows()).thenReturn(workflowsMap);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            WorkflowIdGenerator.generateWorkflowId(workflowName, configMock));

        assertEquals("Unknown key generation strategy: INVALID_STRATEGY", exception.getMessage());
    }
}

