package com.github.sardul3.temporal_boot.common.config;

import com.github.sardul3.temporal_boot.common.activities.PublishBannerMessageActivitiesImpl;
import com.github.sardul3.temporal_boot.common.activities.SchedulePaymentActivitiesImpl;
import com.github.sardul3.temporal_boot.common.workflows.PublishBannerMessageWorkflowImpl;
import com.github.sardul3.temporal_boot.common.workflows.SchedulePaymentWorkflowImpl;

import io.grpc.StatusRuntimeException;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import lombok.extern.slf4j.Slf4j;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;

/**
 * TemporalConfig is a Spring Boot configuration class responsible for setting up the
 * Temporal components needed for interacting with Temporal workflows and activities.
 *
 * This configuration defines:
 * - A `WorkflowClient` bean to connect to the Temporal service.
 * - A `WorkerFactory` bean to manage and create workers.
 * - A `Worker` bean that registers workflows and activities to handle tasks in the `ScheduledPaymentQueue`.
 *
 * This configuration enables the application to poll for workflow and activity tasks
 * and execute them based on the registered implementations.
 *
 * <p><b>Author's Intent:</b></p>
 * The intent of this configuration is to seamlessly integrate Temporal into a Spring Boot
 * application. It allows for easy management of workflows and activities by leveraging
 * Spring's dependency injection and Temporal's worker-based task execution.
 *
 * This setup abstracts away the complexity of managing Temporal workers and makes it
 * easier to work with Temporal in a Spring Boot environment by registering the necessary
 * beans for workflow execution and activity handling.
 */
@Configuration
@EnableRetry
@Slf4j
public class TemporalConfig {

    /**
     * Creates and configures a {@link WorkflowClient} bean that connects to the Temporal service.
     * The WorkflowClient is used to interact with workflows from the application, allowing
     * the application to start, signal, or query workflows.
     *
     * @return a new instance of {@link WorkflowClient} connected to the local Temporal service.
     */
    @Bean
    public WorkflowClient workflowClient(TemporalConfigProperties temporalConfigProperties) {
        WorkflowServiceStubsOptions stubOptions = WorkflowServiceStubsOptions.newBuilder()
            .setTarget(temporalConfigProperties.getServer()) 
            .setRpcTimeout(Duration.ofSeconds(30))
            .build();
        
        log.info("Attempting to connect to Temporal server...");
        WorkflowServiceStubs service = WorkflowServiceStubs.newServiceStubs(stubOptions);

        TemporalNameSpaceManagement namespaceHelper = new TemporalNameSpaceManagement(service);
        namespaceHelper.namespace(temporalConfigProperties.getNamespace(), 30);  // Retention period of 30 days

        WorkflowClientOptions clientOptions = WorkflowClientOptions.newBuilder()
            .setNamespace(temporalConfigProperties.getNamespace())
            .build();
        
        log.info("Connected to Temporal server successfully.");
        return WorkflowClient.newInstance(service, clientOptions);
    }

    /**
     * Creates and configures a {@link WorkerFactory} bean that is responsible for creating and managing workers.
     * The WorkerFactory allows the application to define workers that poll task queues for workflow tasks.
     *
     * @param workflowClient the WorkflowClient that is used to connect workers to the Temporal service.
     * @return a new instance of {@link WorkerFactory}.
     */
    @Bean
    public WorkerFactory workerFactory(WorkflowClient workflowClient) {
        if (workflowClient == null) {
            log.warn("Temporal client is null. Worker factory will not be created.");
            return null;
        }
        return WorkerFactory.newInstance(workflowClient);
    }
}
