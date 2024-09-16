// package com.github.sardul3.temporal_boot.common.workflows;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertThrows;

// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.RegisterExtension;

// import com.github.sardul3.temporal_boot.api.dtos.BannerChangeRequest;
// import com.github.sardul3.temporal_boot.common.activities.PublishBannerMessageActivitiesImpl;

// import io.temporal.client.WorkflowException;
// import io.temporal.testing.TestWorkflowEnvironment;
// import io.temporal.testing.TestWorkflowExtension;
// import io.temporal.worker.Worker;


// public class PublishBannerMessageWorkflowImplTest {

//     @RegisterExtension
//     public static final TestWorkflowExtension workflowExtension =
//         TestWorkflowExtension.newBuilder()
//             .setWorkflowTypes(PublishBannerMessageWorkflowImpl.class)
//             .setDoNotStart(true)
//             .build();

//     @BeforeEach
//     void setUp(TestWorkflowEnvironment testEnv, Worker worker, PublishBannerMessageWorkflow workflow) {
//         worker.registerActivitiesImplementations(new PublishBannerMessageActivitiesImpl());
//         testEnv.start();
//     }

//     @AfterEach
//     void cleanup(TestWorkflowEnvironment testEnv) {
//         if(testEnv != null) {
//             testEnv.shutdown();
//         }
//     }

//   @Test
//   public void testValidBannerName(TestWorkflowEnvironment testEnv, Worker worker, PublishBannerMessageWorkflow workflow) {
//    BannerChangeRequest request = new BannerChangeRequest();
//    request.setBannerMessage("Invalid.. or am I??");
//    String result = workflow.createAndPublishBannerMessage(request);
//    String expected = "PUBLISHED";
//    assertEquals(expected, result);
//   }

//   @Test
//   public void testInvalidBannerName(TestWorkflowEnvironment testEnv, Worker worker, PublishBannerMessageWorkflow workflow) {
//     BannerChangeRequest request = new BannerChangeRequest();
//     request.setBannerMessage("Invalid");
//     assertThrows(WorkflowException.class, () -> {
//         workflow.createAndPublishBannerMessage(request);
//     });
//   }
// }
