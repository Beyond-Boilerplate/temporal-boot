// package com.github.sardul3.temporal_boot.common.activities;

// import static org.junit.Assert.assertEquals;
// import static org.junit.Assert.assertThrows;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;

// import io.temporal.failure.ActivityFailure;
// import io.temporal.testing.TestActivityEnvironment;

// public class PublishBannerMessageActivitiesImplTest {
//     private TestActivityEnvironment testActivityEnvironment;
//     private PublishBannerMessageActivities activity;
    

//     @BeforeEach
//     void setup() {
//         testActivityEnvironment = TestActivityEnvironment.newInstance();
//         testActivityEnvironment.registerActivitiesImplementations(new PublishBannerMessageActivitiesImpl());
//         activity = testActivityEnvironment.newActivityStub(PublishBannerMessageActivities.class);
//     }

//     @Test
//     @DisplayName("Banner message of length greater 10 should return true")
//     void testValidBannerMessageLength() {
//         // given
//         String bannerMessage = "I am a valid message!";

//         // when
//         boolean result = activity.checkBannerLengthIsAppropriate(bannerMessage);
//         boolean expected = true;

//         // then
//         assertEquals(expected, result);
//     }

//     @Test
//     @DisplayName("Banner message of length 10 should throw exception")
//     void testInvalidBannerMessageLength() {
//         // given
//         String bannerMessage = "I am a...";

//         // then
//         assertThrows(ActivityFailure.class, () -> {
//             // when
//             activity.checkBannerLengthIsAppropriate(bannerMessage);
//         });
//     }
// }
