package com.github.sardul3.temporal_boot.common.workflows;

import java.time.Duration;

import com.github.sardul3.temporal_boot.common.activities.PublishBannerMessageActivities;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.ActivityStub;
import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;

public class PublishBannerMessageWorkflowImpl implements PublishBannerMessageWorkflow {

    /* 
     * At least one of the following options needs to be defined:
     * - setStartToCloseTimeout
     * - setScheduleToCloseTimeout
     */
    private final ActivityOptions options = ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofMinutes(3))
            .build();

    /*
     * Activity stubs are proxies for activity invocations that
     * are executed outside of the workflow thread on the activity worker, that can be on a
     * different host. Temporal is going to dispatch the activity results back to the workflow and
     * unblock the stub as soon as activity is completed on the activity worker.
     * 
     * The activity options that were defined above are passed in as a parameter.
     */
    PublishBannerMessageActivities activities = Workflow.newActivityStub(PublishBannerMessageActivities.class, options);

    // This is the entry point to the Workflow.
    @Override
    public String createAndPublishBannerMessage(String message) {

        // syncronious execution (blocking)
        activities.checkBannerLengthIsAppropriate(message);
        activities.checkRequiredApprovals(message);

        //asycronous (non-blocking)
        Promise<String> newMessage = Async.function(activities::correctBannerFormatting, message);
        
        activities.correctBannerFormatting(message);
        activities.ensureCurrentBannerMessageIsNotSame(message);
        activities.saveBannerMessage(message);
        return activities.publishBannerMessage(message);
    }
    
}
