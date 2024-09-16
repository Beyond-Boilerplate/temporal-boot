package com.github.sardul3.temporal_boot.common.activities;

import java.util.List;

import org.springframework.stereotype.Component;

import com.github.sardul3.temporal_boot.app.repos.ApprovalRepository;
import com.github.sardul3.temporal_boot.app.repos.AuditTrailRepository;
import com.github.sardul3.temporal_boot.app.repos.BannerChangeRequestRepository;
import com.github.sardul3.temporal_boot.app.repos.CurrentBannerRepository;
import com.github.sardul3.temporal_boot.app.repos.CustomerRequestStatusRepository;
import com.github.sardul3.temporal_boot.common.models.Approval;
import com.github.sardul3.temporal_boot.common.models.AuditTrail;
import com.github.sardul3.temporal_boot.common.models.BannerChangeRequest;
import com.github.sardul3.temporal_boot.common.models.CurrentBanner;
import com.github.sardul3.temporal_boot.common.models.CustomerRequestStatus;
import java.time.LocalDateTime;

import io.temporal.failure.ApplicationFailure;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class PublishBannerMessageActivitiesImpl implements PublishBannerMessageActivities {

    
    private final ApprovalRepository approvalRepository;

    private final BannerChangeRequestRepository bannerChangeRequestRepository;

    private final CurrentBannerRepository currentBannerRepository;

    private final AuditTrailRepository auditTrailRepository;

    private final CustomerRequestStatusRepository customerRequestStatusRepository;

    @Override
    public boolean checkBannerLengthIsAppropriate(String bannerMessage) {
        if(bannerMessage.length() < 10) {   
            // Throwing a non-retryable exception using ApplicationFailure
            throw ApplicationFailure.newNonRetryableFailure("Banner length too small", "BannerLengthTooSmallException");
        }
        return true;
    }

    @Override
    public boolean checkRequiredApprovals(String bannerMessage, String itemCategory) {
        List<Approval> approvers = approvalRepository.findByItemCategory(itemCategory);
        boolean approvalsNeeded = !approvers.isEmpty();
        log.info("Approvals required for category '{}': {}", itemCategory, approvalsNeeded);
        return approvalsNeeded;
    }

    @Override
    public String correctBannerFormatting(String bannerMessage) {
        String formattedMessage = bannerMessage.strip() + ".";
        log.info("Banner message formatted to: {}", formattedMessage);
        return formattedMessage;
    }

    @Override
    public boolean ensureCurrentBannerMessageIsNotSame(String itemId, String newBannerMessage) {
        CurrentBanner currentBanner = currentBannerRepository.findByItemId(Long.parseLong(itemId));
        if (currentBanner != null && currentBanner.getCurrentBannerName().equals(newBannerMessage)) {
            log.warn("New banner message '{}' is the same as the current banner for item '{}'", newBannerMessage, itemId);
            return false;
        }
        log.info("New banner message is different from the current banner for item '{}'", itemId);
        return true;
    }

    @Override
    public void saveBannerChangeRequest(String itemId, String bannerMessage, String itemCategory, LocalDateTime applyDateTime, String correlationId) {
        BannerChangeRequest bannerChangeRequest = new BannerChangeRequest();
        bannerChangeRequest.setItemId(Long.parseLong(itemId));
        bannerChangeRequest.setBannerName(bannerMessage);
        bannerChangeRequest.setApplyDateTime(applyDateTime);
        bannerChangeRequest.setStatus("Pending");
        bannerChangeRequest.setApprovalStatus("Pending");
        bannerChangeRequest.setPriority("medium");
        bannerChangeRequestRepository.save(bannerChangeRequest);
        log.info("Banner change request saved for item '{}'", itemId);
    }

    @Override
    public CurrentBanner saveBannerMessage(String itemId, String bannerMessage) {
        CurrentBanner currentBanner = currentBannerRepository.findByItemId(Long.parseLong(itemId));
        if (currentBanner == null) {
            currentBanner = new CurrentBanner();
            currentBanner.setItemId(Long.parseLong(itemId));
        }
        currentBanner.setCurrentBannerName(bannerMessage);
        currentBanner.setActive(true);
        currentBannerRepository.save(currentBanner);
        log.info("Banner message '{}' saved for item '{}'", bannerMessage, itemId);
        return currentBanner;
    }

    @Override
    public void applyBannerChange(String itemId, String newBannerMessage) {
        saveBannerMessage(itemId, newBannerMessage);
        log.info("Banner change applied for item '{}'", itemId);
    }

    @Override
    public void rejectBannerChange(String itemId, String reason) {
        BannerChangeRequest request = bannerChangeRequestRepository.findById(Long.parseLong(itemId))
                .orElseThrow(() -> new IllegalArgumentException("Request not found for item: " + itemId));

        request.setStatus("Rejected");
        request.setApprovalStatus("Rejected");
        bannerChangeRequestRepository.save(request);

        log.info("Banner change for item '{}' rejected. Reason: {}", itemId, reason);

        updateAuditTrail(itemId, "Rejected: " + reason);
        updateCustomerFacingStatus(itemId, "Rejected");
    }

    @Override
    public void notifyApprovers(List<String> approvers, String bannerMessage) {
        log.info("Notifying approvers: {} for banner message: '{}'", approvers, bannerMessage);
        approvers.forEach(approver -> {
            // Simulate sending an email
            log.info("Email sent to approver: {}", approver);
        });
    }

    @Override
    public void publishBannerEventToQueue(String itemId, String bannerMessage) {
        log.info("Publishing banner event to queue for item '{}': {}", itemId, bannerMessage);
        // Event publication logic here (e.g., Kafka, RabbitMQ)
    }

    @Override
    public void updateRequestStatus(String itemId, String status) {
        BannerChangeRequest request = bannerChangeRequestRepository
            .findByItemId(Long.parseLong(itemId))
            .get(0);

        request.setStatus(status);
        bannerChangeRequestRepository.save(request);
        log.info("Request status for item '{}' updated to '{}'", itemId, status);
    }

    @Override
    public void updateAuditTrail(String itemId, String action) {
        AuditTrail auditTrail = new AuditTrail();
        auditTrail.setRequest( (bannerChangeRequestRepository.findByItemId(Long.parseLong(itemId))).get(0));
                
        auditTrail.setAction(action);
        auditTrail.setPerformedBy("system");
        auditTrail.setTimestamp(LocalDateTime.now());

        auditTrailRepository.save(auditTrail);
        log.info("Audit trail updated for item '{}': {}", itemId, action);
    }

    @Override
    public void updateCustomerFacingStatus(String itemId, String status) {
        CustomerRequestStatus customerRequestStatus = customerRequestStatusRepository.findByRequestId(Long.parseLong(itemId));
        if (customerRequestStatus == null) {
            customerRequestStatus = new CustomerRequestStatus();
            customerRequestStatus.setRequestId(Long.parseLong(itemId));
            customerRequestStatus.setCustomerId(123L);  // Placeholder for customer ID
            customerRequestStatus.setCreatedAt(LocalDateTime.now());
        }

        customerRequestStatus.setStatus(status);
        customerRequestStatus.setLastUpdated(LocalDateTime.now());

        customerRequestStatusRepository.save(customerRequestStatus);
        log.info("Customer-facing status for item '{}' updated to '{}'", itemId, status);
    }

    @Override
    public List<String> getApproversForCategory(String itemCategory) {
        List<Approval> approvals = approvalRepository.findByItemCategory(itemCategory);
        return approvals.stream().map(Approval::getApproverUsername).toList();
    }

    @Override
    public boolean checkIfItemExists(String itemId) {
        return currentBannerRepository.existsById(Long.parseLong(itemId));
    }
    
}
