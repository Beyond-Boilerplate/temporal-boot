package com.github.sardul3.temporal_boot.common.config;

import com.google.protobuf.util.Durations;

import io.temporal.api.workflowservice.v1.DescribeNamespaceRequest;
import io.temporal.api.workflowservice.v1.DescribeNamespaceResponse;
import io.temporal.api.workflowservice.v1.RegisterNamespaceRequest;
import io.temporal.serviceclient.WorkflowServiceStubs;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class TemporalNameSpaceManagement {

    private final WorkflowServiceStubs service;

   /**
     * Ensures that a namespace exists in Temporal. If the namespace doesn't exist, it creates one.
     * 
     * @param namespace The namespace to check or create.
     * @param retentionDays The retention period in days for the workflow execution event history.
     */
    public void ensureNamespaceExists(String namespace, int retentionDays) {
        if (!doesNamespaceExist(namespace)) {
            log.info("Namespace '{}' does not exist. Creating it...", namespace);
            createNamespace(namespace, retentionDays);
        } else {
            log.info("Namespace '{}' already exists. Skipping creation.", namespace);
        }
    }

    /**
     * Checks if a namespace exists in Temporal by attempting to describe it.
     * 
     * @param namespace The namespace to check.
     * @return True if the namespace exists, false otherwise.
     */
    private boolean doesNamespaceExist(String namespace) {
        try {
            DescribeNamespaceRequest describeNamespaceRequest = DescribeNamespaceRequest.newBuilder()
                .setNamespace(namespace)
                .build();
            DescribeNamespaceResponse response = service.blockingStub().describeNamespace(describeNamespaceRequest);
            log.info("Namespace '{}' found with description: {}", namespace, response.getNamespaceInfo().getDescription());
            return true;
        } catch (io.grpc.StatusRuntimeException e) {
            if (e.getStatus().getCode() == io.grpc.Status.Code.NOT_FOUND) {
                log.info("Namespace '{}' not found.", namespace);
                return false;
            }
            log.error("Failed to describe namespace '{}': {}", namespace, e.getMessage(), e);
            throw e;  // Propagate exception for any other errors
        }
    }

    /**
     * Registers a new namespace in Temporal.
     * 
     * @param namespace The namespace to create.
     * @param retentionDays The retention period in days for the workflow execution event history.
     */
    private void createNamespace(String namespace, int retentionDays) {
        try {
            RegisterNamespaceRequest registerNamespaceRequest = RegisterNamespaceRequest.newBuilder()
                .setNamespace(namespace)
                .setWorkflowExecutionRetentionPeriod(Durations.fromDays(retentionDays))  // Set retention period
                .build();
            
            service.blockingStub().registerNamespace(registerNamespaceRequest);
            log.info("Namespace '{}' successfully created with a retention period of {} days.", namespace, retentionDays);
        } catch (Exception e) {
            log.error("Failed to create namespace '{}': {}", namespace, e.getMessage(), e);
            throw e;
        }
    }
    
}
