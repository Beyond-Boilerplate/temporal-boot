package com.github.sardul3.temporal_boot.common.utils;

public class TemporalConstants {
    public static class Workflows {
        public static final String PAYMENT_SCHEDULING_WORKFLOW = "paymentSchedulingWorkflow";
        public static final String PUBLISH_BANNER_MESSAGE_WORKFLOW = "publishBannerMessageWorkflow";
        public static final String DEFAULT_VERSION = "default";
        public static final String VERSION_1 = "v1";
    }

    public static class Activities {
        public static final String SCHEDULE_PAYMENT_ACTIVITIES = "schedulePaymentActivities";
        public static final String PUBLISH_BANNER_MESSAGE_ACTIVITIES = "publishBannerMessageActivities";
    }

    public static class KeyGenerationStrategies {
        public static final String UUID = "UUID";
    }
    
    public static class Workers {
        public static final String SCHEDULE_PAYMENT_WORKER =  "paymentScheduleWorker";
        public static final String PUBLISH_BANNER_MESSAGE_WORKER =  "publishBannerWorker";
    }
}
