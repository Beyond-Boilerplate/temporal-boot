package com.github.sardul3.temporal_boot.workers;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;

import com.github.sardul3.temporal_boot.common.activities.ProductDataImprovementActivitiesImpl;
import com.github.sardul3.temporal_boot.common.config.TemporalConfigProperties;
import com.github.sardul3.temporal_boot.common.utils.TemporalConstants;
import com.github.sardul3.temporal_boot.common.utils.TemporalOptionsHelper;
import com.github.sardul3.temporal_boot.common.workflows.ProductDataImprovementWorkflow;
import com.github.sardul3.temporal_boot.common.workflows.ProductDataImprovementWorkflowImpl;

import io.temporal.activity.ActivityOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.github.sardul3.temporal_boot.*"
})
@AllArgsConstructor
@Profile("product-data-improvement-worker")
@Slf4j
public class ProductDataImprovementWorker implements CommandLineRunner {

    private final WorkerFactory workerFactory;
    private final ProductDataImprovementActivitiesImpl productDataImprovementActivitiesImpl;
    private final TemporalConfigProperties temporalConfigProperties;
    private final TemporalOptionsHelper optionsHelper;


    public static void main(String args[]) {
        SpringApplication.run(ProductDataImprovementWorker.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Create a worker that listens to the Product Data Improvement task queue
        // Worker worker = workerFactory.newWorker(TemporalTaskQueues.PRODUCT_DATA_IMPROVEMENT_QUEUE);
        String taskQueue = temporalConfigProperties.getWorkers()
                .get(TemporalConstants.Workers.PRODUCT_DATA_IMPROVEMENT_WORKER).getTaskQueue();

        Worker worker = workerFactory.newWorker(taskQueue);

        // Register workflow and activity implementations
        registerWorkflowsAndActivities(worker);

        log.info("Starting Product Data Improvement Worker...");

        // Start polling for tasks
        workerFactory.start();

        log.info("Product Data Improvement Worker started successfully.");
    }

    void registerWorkflowsAndActivities(Worker worker) {
        Map<String, ActivityOptions> activityOptionsMap = createActivityOptionsMap();
        
        worker.registerWorkflowImplementationFactory(ProductDataImprovementWorkflow.class, () -> 
            new ProductDataImprovementWorkflowImpl(activityOptionsMap)
        );

        worker.registerActivitiesImplementations(productDataImprovementActivitiesImpl);
    }

    Map<String, ActivityOptions> createActivityOptionsMap() {
        Map<String, ActivityOptions> activityOptionsMap = new HashMap<>();
        activityOptionsMap.put("productDataImprovementActivities", optionsHelper.createActivityOptions("productDataImprovementActivities", "default"));
        return activityOptionsMap;
    }
}
