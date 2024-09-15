package com.github.sardul3.temporal_boot.common.activities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mock;

import com.github.sardul3.temporal_boot.app.services.TransactionService;

import io.temporal.failure.ActivityFailure;
import io.temporal.testing.TestActivityExtension;

public class SchedulePaymentActivitiesImplTest {

    @Mock
    private TransactionService transactionService;

    @RegisterExtension
    private final TestActivityExtension activityExtension =
        TestActivityExtension.newBuilder()
            .setActivityImplementations(new SchedulePaymentActivitiesImpl(transactionService))
            .build();

    @Test
    @DisplayName("Scheduled payment value must be greater than 0")
    void testValidateAmountWithAboveZeroAmount(SchedulePaymentActivities activity) {
        // given
        double amount = 1;

        // when
        boolean result = activity.validateAmount(amount);
        
        //then
        boolean expected = true;
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Scheduled payment value must be greater than 0")
    void testValidateAmountWithLessThanZero(SchedulePaymentActivities activity) {
        // given
        double amount = -1;

        // assert
        assertThrows(ActivityFailure.class, () -> {
            // when
            boolean result = activity.validateAmount(amount);
            
        });
    }
}
