package com.github.sardul3.temporal_boot.common.workflows;

import java.time.Duration;

import org.slf4j.Logger;

import com.github.sardul3.temporal_boot.api.dtos.ApprovalSignal;
import com.github.sardul3.temporal_boot.api.dtos.BannerChangeRequest;
import com.github.sardul3.temporal_boot.common.activities.PublishBannerMessageActivities;

import java.util.*;
import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;
import java.time.LocalDateTime;


public class PublishBannerMessageWorkflowImpl implements PublishBannerMessageWorkflow {

    // Workflow logger prevents duplicate logs during replays
    private static final Logger log = Workflow.getLogger(PublishBannerMessageWorkflow.class);

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
    PublishBannerMessageActivities activities = 
        Workflow.newActivityStub(PublishBannerMessageActivities.class, options);

        private final Set<String> approvers = new HashSet<>();
        private boolean approvalReceived = false;
        private int requiredApprovals;
    
        @Override
        public String createAndPublishBannerMessage(BannerChangeRequest request, String correlationId) {
            String itemId = request.getItemId();
            String bannerMessage = request.getBannerMessage();
            String itemCategory = request.getItemCategory();
            LocalDateTime applyDate = request.getApplyDateTime();

            if(!activities.checkIfItemExists(itemId)) {
                return "Item does not exist - no action necessary";
            }
    
            // Step 1: Check banner length
            activities.checkBannerLengthIsAppropriate(bannerMessage);
    
            // Parallel execution: Correct banner formatting and ensure it's not the same as the current one
            Promise<String> correctedBanner = Async.function(activities::correctBannerFormatting, bannerMessage);
            String correctedBannerValue = correctedBanner.get();

            Promise<Boolean> currentBannerCheck = Async.function(activities::ensureCurrentBannerMessageIsNotSame, itemId, correctedBannerValue);
    
    
            // If the banner message is the same as the current one, update status and end the workflow
            if (!currentBannerCheck.get()) {
                activities.saveBannerChangeRequest(itemId, bannerMessage, itemCategory, applyDate, correlationId);
                activities.updateRequestStatus(itemId, "nothing-to-apply");
                activities.updateCustomerFacingStatus(itemId, "Nothing to apply");
                activities.updateAuditTrail(itemId, "No changes to apply");
                log.info("No banner change needed for item '{}'", itemId);
                return "No banner change needed";
            }

            activities.saveBannerChangeRequest(itemId, bannerMessage, itemCategory, applyDate, correlationId);
    
            // Step 2: Check for required approvers and notify
            boolean approvalNeeded = activities.checkRequiredApprovals(bannerMessage, itemCategory);
            List<String> approversList = activities.getApproversForCategory(itemCategory);
    
            if (approvalNeeded && !approversList.isEmpty()) {
                activities.notifyApprovers(approversList, bannerMessage);
    
                // Step 3: Sleep until halfway through the apply date
                long totalSleepTime = calculateTimeUntil(applyDate);
                long halfwaySleepTime = totalSleepTime / 2;
    
                Workflow.sleep(Duration.ofMillis(halfwaySleepTime));
    
                // Send reminders halfway through if no approvals yet
                if (!approvalReceived) {
                    activities.notifyApprovers(approversList, "Reminder: Please approve the banner message: " + bannerMessage);
                }
    
                // Step 4: Sleep until the apply date and wake up
                Workflow.sleep(Duration.ofMillis(halfwaySleepTime));
            }
    
            // Step 5: After waking up, check for approvals
            if (approvalReceived || approvers.size() >= requiredApprovals) {
                // Save the banner message
                activities.saveBannerMessage(itemId, correctedBanner.get());
                activities.applyBannerChange(itemId, correctedBanner.get());
    
                // Step 6: Update all the tables
                activities.updateRequestStatus(itemId, "Applied");
                activities.updateAuditTrail(itemId, "Banner Change Applied");
                activities.updateCustomerFacingStatus(itemId, "Completed");
    
                // Step 7: Publish event to the event queue
                activities.publishBannerEventToQueue(itemId, correctedBanner.get());
    
                log.info("Banner message for item '{}' applied and published.", itemId);
                return "Banner change applied successfully.";
            } else {
                // If approvals are missing, reject the request
                activities.rejectBannerChange(itemId, "Approvals missing");
    
                // Update relevant tables
                activities.updateRequestStatus(itemId, "Rejected");
                activities.updateAuditTrail(itemId, "Banner Change Rejected");
                activities.updateCustomerFacingStatus(itemId, "Rejected");
    
                return "Banner change rejected due to missing approvals.";
            }
        }
    
        @Override
        public void signalApproval(ApprovalSignal approvalSignal) {
            approvers.add(approvalSignal.getApproverUsername());
            approvalReceived = approvers.size() >= requiredApprovals;
            log.info("Approval received from {}", approvalSignal.getApproverUsername());
        }
    
        private long calculateTimeUntil(LocalDateTime applyDate) {
            // Logic to calculate time until the applyDate from the current time
            // Placeholder for demonstration purposes
            return Duration.ofMinutes(1).toMillis(); // Placeholder: 24 hours
        }
    }