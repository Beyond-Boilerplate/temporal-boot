package com.github.sardul3.temporal_boot.app.services;

import com.github.sardul3.temporal_boot.api.dtos.AIWorkflowRequest;
import com.github.sardul3.temporal_boot.api.dtos.AIWorkflowResponse;
import com.github.sardul3.temporal_boot.common.config.TemporalConfigProperties;
import com.github.sardul3.temporal_boot.common.utils.WorkflowIdGenerator;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenericWorkflowService {

    private final WorkflowClient workflowClient;
    private final TemporalConfigProperties config;

    public <T> AIWorkflowResponse startWorkflow(
            AIWorkflowRequest request,
            Class<T> workflowClass,
            String correlationId
    ) {
        String workflowName = resolveWorkflowName(workflowClass.getSimpleName());
        String workflowId = WorkflowIdGenerator.generateWorkflowId(workflowName, config);

        String taskQueue = resolveTaskQueue(workflowName);

        T workflow = workflowClient.newWorkflowStub(
            workflowClass,
            WorkflowOptions.newBuilder()
                .setWorkflowId(workflowId)
                .setTaskQueue(taskQueue)
                .build()
        );

        // Start async: assumes all workflows follow a method like "runImprovementWorkflow(FileReference, UseCaseIdentifier)"
        WorkflowClient.start(
            () -> invokeRunMethod(workflow, request)
        );

        AIWorkflowResponse response = new AIWorkflowResponse();
        response.setWorkflowId(workflowId);
        return response;
    }

    public <T> String queryWorkflowStatus(Class<T> workflowClass, String workflowId) {
        T workflow = workflowClient.newWorkflowStub(workflowClass, workflowId);

        try {
            return (String) workflowClass.getMethod("getWorkflowStatus").invoke(workflow);
        } catch (Exception e) {
            throw new RuntimeException("Unable to query workflow status", e);
        }
    }

    private void invokeRunMethod(Object workflow, AIWorkflowRequest request) {
        try {
            workflow.getClass()
                    .getMethod("runImprovementWorkflow",
                               request.getFileReference().getClass(),
                               request.getUseCase().getClass())
                    .invoke(workflow, request.getFileReference(), request.getUseCase());
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke workflow entry method", e);
        }
    }

    private String resolveTaskQueue(String workflowName) {
        String version = config.getCurrentVersions().getWorkflows().get(workflowName);
        return config.getWorkflows()
                .get(workflowName)
                .getVersions()
                .get(version)
                .getTaskQueue();
    }

    private String resolveWorkflowName(String className) {
        // Check if there's a mapping for this class name
        String mappedName = config.getCurrentVersions().getUseCases().get(className);
        if (mappedName != null) {
            return mappedName;
        }
        // If no mapping found, return the original class name
        return className;
    }
}

