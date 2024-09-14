// package com.github.sardul3.temporal_boot.common.workflows;

// import static org.junit.jupiter.api.Assertions.assertEquals;

// import org.junit.Test;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.TestInstance;
// import org.junit.jupiter.api.extension.RegisterExtension;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.context.ConfigurableApplicationContext;
// import org.springframework.context.annotation.ComponentScan;
// import org.springframework.test.annotation.DirtiesContext;

// import com.github.sardul3.temporal_boot.common.activities.PublishBannerMessageActivitiesImpl;

// import io.temporal.client.WorkflowClient;
// import io.temporal.client.WorkflowOptions;
// import io.temporal.testing.TestWorkflowEnvironment;
// import io.temporal.testing.TestWorkflowExtension;
// import io.temporal.worker.Worker;
// import junit.framework.Assert;

// @SpringBootTest
// @TestInstance(TestInstance.Lifecycle.PER_CLASS)
// @DirtiesContext
// public class PublishBannerMessageWorkflowImplTest {

//     @Autowired ConfigurableApplicationContext applicationContext;

//     @Autowired TestWorkflowEnvironment testWorkflowEnvironment;

//     @Autowired WorkflowClient workflowClient;

//     @BeforeEach
//     void setUp() {
//         applicationContext.start();
//     }

//   @Test
//   public void testHello() {
//     PublishBannerMessageWorkflow workflow =
//         workflowClient.newWorkflowStub(
//             PublishBannerMessageWorkflow.class,
//             WorkflowOptions.newBuilder()
//                 .setTaskQueue("HelloSampleTaskQueue")
//                 .setWorkflowId("HelloSampleTest")
//                 .build());
//     String result = workflow.createAndPublishBannerMessage("User");
//   }

//   @ComponentScan
//   public static class Configuration {}
// }
