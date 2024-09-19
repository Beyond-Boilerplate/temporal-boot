package com.github.sardul3.temporal_boot.workers;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;

import com.github.sardul3.temporal_boot.common.activities.PublishBannerMessageActivitiesImpl;
import com.github.sardul3.temporal_boot.common.config.TemporalConfigProperties;
import com.github.sardul3.temporal_boot.common.utils.TemporalConstants;
import com.github.sardul3.temporal_boot.common.workflows.PublishBannerMessageWorkflowImpl;

import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.github.sardul3.temporal_boot.*"
})
@AllArgsConstructor
@Profile("publish-banner-worker")
@Slf4j
public class PublishBannerMessageWorker implements CommandLineRunner {
    
    private final WorkerFactory workerFactory;
    private final PublishBannerMessageActivitiesImpl publishBannerMessageActivitiesImpl;
    private final TemporalConfigProperties temporalConfigProperties;

    public static void main(String args[]) {
        SpringApplication.run(PublishBannerMessageWorker.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Create a worker that listens to the Banner Message Submission task queue
        // Worker worker = workerFactory.newWorker(TemporalTaskQueues.BANNER_MESSAGE_SUBMISSION_QUEUE);
        String taskQueue = temporalConfigProperties.getWorkers()
                .get(TemporalConstants.Workers.PUBLISH_BANNER_MESSAGE_WORKER).getTaskQueue();

        Worker worker = workerFactory.newWorker(taskQueue);

        // Register workflow and activity implementations
        worker.registerWorkflowImplementationTypes(PublishBannerMessageWorkflowImpl.class);
        worker.registerActivitiesImplementations(publishBannerMessageActivitiesImpl);

        log.info("Starting Publish Banner Message Worker...");

        // Start polling for tasks
        workerFactory.start();

        log.info("Publish Banner Message Worker started successfully.");
    }
}
