package com.github.sardul3.temporal_boot.workers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;

import com.github.sardul3.temporal_boot.common.activities.SchedulePaymentActivitiesImpl;
import com.github.sardul3.temporal_boot.common.config.TemporalConfigProperties;
import com.github.sardul3.temporal_boot.common.utils.TemporalConstants;
import com.github.sardul3.temporal_boot.common.utils.TemporalOptionsHelper;
import com.github.sardul3.temporal_boot.common.workflows.SchedulePaymentWorkflow;
import com.github.sardul3.temporal_boot.common.workflows.SchedulePaymentWorkflowImpl;

import io.temporal.activity.ActivityOptions;
import io.temporal.client.WorkflowOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.workflow.Workflow;
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
    private final TemporalConfigProperties temporalConfigProperties;
    private final TemporalOptionsHelper optionsHelper;

    public static void main(String[] args) {
        SpringApplication.run(SchedulePaymentWorker.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String taskQueue = temporalConfigProperties.getWorkers()
                .get(TemporalConstants.Workers.SCHEDULE_PAYMENT_WORKER).getTaskQueue();

        Worker worker = workerFactory.newWorker(taskQueue);

        // Register workflow and activity implementations
        registerWorkflowsAndActivities(worker);

        log.info("Starting Payment schedule Worker...");

        // Start polling for tasks
        workerFactory.start();

        log.info("Payment schedule Worker started successfully.");
    }

    void registerWorkflowsAndActivities(Worker worker) {
        Map<String, ActivityOptions> activityOptionsMap = createActivityOptionsMap();
        
        worker.registerWorkflowImplementationFactory(SchedulePaymentWorkflow.class, () -> 
            new SchedulePaymentWorkflowImpl(activityOptionsMap)
        );

        worker.registerActivitiesImplementations(schedulePaymentActivitiesImpl);
    }

    Map<String, ActivityOptions> createActivityOptionsMap() {
        Map<String, ActivityOptions> activityOptionsMap = new HashMap<>();
        activityOptionsMap.put("schedulePaymentActivities", optionsHelper.createActivityOptions("schedulePaymentActivities", "v1"));
        activityOptionsMap.put("schedulePaymentActivitiesNoRetry", optionsHelper.createActivityOptions("schedulePaymentActivitiesNoRetry", "v1"));
        return activityOptionsMap;
    }
}
