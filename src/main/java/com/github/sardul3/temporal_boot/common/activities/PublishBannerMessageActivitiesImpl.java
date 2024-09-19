package com.github.sardul3.temporal_boot.common.activities;

import org.springframework.stereotype.Component;

import io.temporal.failure.ApplicationFailure;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PublishBannerMessageActivitiesImpl implements PublishBannerMessageActivities {

    @Override
    public boolean checkBannerLengthIsAppropriate(String bannerMessage) {
        if(bannerMessage.length() < 10) {   
            // Throwing a non-retryable exception using ApplicationFailure
            throw ApplicationFailure.newNonRetryableFailure("Banner length too small", "BannerLengthTooSmallException");
        }
        return true;
    }

    @Override
    public boolean checkRequiredApprovals(String bannerMessage) {
        return false;
    }

    @Override
    public String correctBannerFormatting(String bannerMessage) {
        return bannerMessage.strip() + ".";
    }

    @Override
    public boolean ensureCurrentBannerMessageIsNotSame(String bannerMessage) {
        return true;
    }

    @Override
    public String saveBannerMessage(String bannerMessage) {
        log.info("Baanner message saved");
        return "SAVED";
    }

    @Override
    public String publishBannerMessage(String bannerMessage) {
        return "PUBLISHED";
    }
    
}
