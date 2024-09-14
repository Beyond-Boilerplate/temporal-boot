package com.github.sardul3.temporal_boot.common.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface PublishBannerMessageActivities {

    @ActivityMethod(name = "CheckBannerMsgLength")
    boolean checkBannerLengthIsAppropriate(String bannerMessage);

    @ActivityMethod
    boolean checkRequiredApprovals(String bannerMessage);

    @ActivityMethod(name = "CheckBannerTextFormatting")
    String correctBannerFormatting(String bannerMessage);

    boolean ensureCurrentBannerMessageIsNotSame(String bannerMessage);

    @ActivityMethod
    String saveBannerMessage(String bannerMessage);

    @ActivityMethod
    String publishBannerMessage(String bannerMessage);
    
}
