// package com.github.sardul3.temporal_boot.worker;

// import com.github.sardul3.temporal_boot.common.activities.SchedulePaymentActivitiesImpl;
// import com.github.sardul3.temporal_boot.common.workflows.SchedulePaymentWorkflowImpl;
// import io.temporal.client.WorkflowClient;
// import io.temporal.serviceclient.WorkflowServiceStubs;
// import io.temporal.worker.Worker;
// import io.temporal.worker.WorkerFactory;

// import org.springframework.boot.CommandLineRunner;
// import org.springframework.boot.SpringApplication;
// import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.boot.autoconfigure.domain.EntityScan;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.ComponentScan;
// import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

// @SpringBootApplication
// @ComponentScan(basePackages = {
// 		"com.github.sardul3.temporal_boot.common"})
// public class WorkerApplication  implements CommandLineRunner{

//     private final WorkerFactory workerFactory;

//     public WorkerApplication(WorkerFactory workerFactory) {
//         this.workerFactory = workerFactory;
//     }

//     public static void main(String[] args) {
//         SpringApplication.run(WorkerApplication.class, args);
//     }


//     @Override
// 	public void run(String... args) throws Exception {
// 		workerFactory.start();
// 	}

// }
