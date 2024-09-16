package com.github.sardul3.temporal_boot.common.workflows;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.sardul3.temporal_boot.common.activities.PublishBannerMessageActivities;
import com.github.sardul3.temporal_boot.common.activities.PublishBannerMessageActivitiesImpl;

import io.temporal.client.WorkflowException;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.testing.TestWorkflowExtension;
import io.temporal.worker.Worker;


public class PublishBannerMessageWorkflowImplMockTest {

    @RegisterExtension
    public static final TestWorkflowExtension workflowExtension =
        TestWorkflowExtension.newBuilder()
            .setWorkflowTypes(PublishBannerMessageWorkflowImpl.class)
            .setDoNotStart(true)
            .build();

    @BeforeEach
    void setUp(TestWorkflowEnvironment testEnv, Worker worker, PublishBannerMessageWorkflow workflow) {
        // testEnv.start();
    }

    @AfterEach
    void cleanup(TestWorkflowEnvironment testEnv) {
        if(testEnv != null) {
            testEnv.shutdown();
        }
    }

//   @Test
//   public void testValidBannerNameWithMock(TestWorkflowEnvironment testEnv, Worker worker, PublishBannerMessageWorkflow workflow) {
//     PublishBannerMessageActivities mockedActivities = 
//         mock(PublishBannerMessageActivities.class, withSettings().withoutAnnotations());
//     when(mockedActivities.checkBannerLengthIsAppropriate(anyString())).thenReturn(true);
//     when(mockedActivities.publishBannerMessage(anyString())).thenReturn("PUBLISHED");
//     worker.registerActivitiesImplementations(mockedActivities);
//     testEnv.start();

//     String result = workflow.createAndPublishBannerMessage("Invalid.. or am I??");
//     String expected = "PUBLISHED";
//     assertEquals(expected, result);
//   }
}
