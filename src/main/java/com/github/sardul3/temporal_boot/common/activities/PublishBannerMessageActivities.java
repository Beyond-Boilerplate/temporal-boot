package com.github.sardul3.temporal_boot.common.activities;

import java.time.LocalDateTime;
import java.util.List;

import com.github.sardul3.temporal_boot.common.models.CurrentBanner;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface PublishBannerMessageActivities {

    @ActivityMethod(name = "CheckBannerMsgLength")
    boolean checkBannerLengthIsAppropriate(String bannerMessage);

    boolean checkRequiredApprovals(String bannerMessage, String itemCategory);

    String correctBannerFormatting(String bannerMessage);

    boolean ensureCurrentBannerMessageIsNotSame(String itemId, String newBannerMessage);

    boolean checkIfItemExists(String itemId);

    CurrentBanner saveBannerMessage(String itemId, String bannerMessage);

    void applyBannerChange(String itemId, String newBannerMessage);

    void rejectBannerChange(String itemId, String reason);

    void notifyApprovers(List<String> approvers, String bannerMessage);

    void publishBannerEventToQueue(String itemId, String bannerMessage);

    void saveBannerChangeRequest(String itemId, String bannerMessage, String itemCategory, LocalDateTime applyDateTime, String correlationId);

    void updateRequestStatus(String itemId, String status);

    void updateAuditTrail(String itemId, String action);

    void updateCustomerFacingStatus(String itemId, String status);

    List<String> getApproversForCategory(String itemCategory);
    
}
