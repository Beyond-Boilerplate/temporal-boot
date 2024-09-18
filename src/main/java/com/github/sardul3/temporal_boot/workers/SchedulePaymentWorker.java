package com.github.sardul3.temporal_boot.workers;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;

import com.github.sardul3.temporal_boot.common.activities.PublishBannerMessageActivitiesImpl;
import com.github.sardul3.temporal_boot.common.activities.SchedulePaymentActivitiesImpl;
import com.github.sardul3.temporal_boot.common.config.TemporalTaskQueues;
import com.github.sardul3.temporal_boot.common.workflows.PublishBannerMessageWorkflowImpl;
import com.github.sardul3.temporal_boot.common.workflows.SchedulePaymentWorkflowImpl;

import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.github.sardul3.temporal_boot.*"
})
@AllArgsConstructor
@Profile("payment-schedule-worker")
@Slf4j
public class SchedulePaymentWorker implements CommandLineRunner {
    
    private final WorkerFactory workerFactory;
    private final SchedulePaymentActivitiesImpl schedulePaymentActivitiesImpl;

    public static void main(String args[]) {
        SpringApplication.run(SchedulePaymentWorker.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Create a worker that listens to the Banner Message Submission task queue
        Worker worker = workerFactory.newWorker(TemporalTaskQueues.PAYMENT_SCHEDULE_QUEUE);

        // Register workflow and activity implementations
        worker.registerWorkflowImplementationTypes(SchedulePaymentWorkflowImpl.class);
        worker.registerActivitiesImplementations(schedulePaymentActivitiesImpl);

        log.info("Starting Payment schedule Worker...");

        // Start polling for tasks
        workerFactory.start();

        log.info("Payment schedule Worker started successfully.");
    }
}
