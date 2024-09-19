package com.github.sardul3.temporal_boot.common.utils;

import java.util.UUID;
import java.util.Map;

import com.github.sardul3.temporal_boot.common.config.TemporalConfigProperties;

public class WorkflowIdGenerator {

    private WorkflowIdGenerator() {}

    public static String generateWorkflowId(String workflowName, TemporalConfigProperties config) {
        // Find the configuration for the specified workflow name
        Map<String, TemporalConfigProperties.WorkflowConfig> workflows = config.getWorkflows();
        TemporalConfigProperties.WorkflowConfig workflowConfig = workflows.get(workflowName);

        if (workflowConfig == null) {
            throw new IllegalArgumentException("Workflow name not found: " + workflowName);
        }

        String prefix = workflowConfig.getPrefix();
        String keyGenerationStrategy = workflowConfig.getKeyGenerationStrategy();

        // Use key generation strategy (currently only UUID is supported, but more can be added)
        String uniqueSuffix = generateKey(keyGenerationStrategy);

        return prefix + "-" + uniqueSuffix;
       
    }

    private static String generateKey(String strategy) {
        // Currently, we support only UUID but you can extend this with more strategies
        if ("UUID".equalsIgnoreCase(strategy)) {
            return UUID.randomUUID().toString();
        }
        throw new IllegalArgumentException("Unknown key generation strategy: " + strategy);
    }
}

