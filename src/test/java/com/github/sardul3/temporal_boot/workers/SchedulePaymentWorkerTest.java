package com.github.sardul3.temporal_boot.workers;

import com.github.sardul3.temporal_boot.common.activities.SchedulePaymentActivitiesImpl;
import com.github.sardul3.temporal_boot.common.config.TemporalConfigProperties;
import com.github.sardul3.temporal_boot.common.utils.TemporalOptionsHelper;
import com.github.sardul3.temporal_boot.common.utils.TemporalConstants;
import com.github.sardul3.temporal_boot.common.workflows.SchedulePaymentWorkflow;
import com.github.sardul3.temporal_boot.common.workflows.SchedulePaymentWorkflowImpl;
import io.temporal.activity.ActivityOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SchedulePaymentWorkerTest {

    @Mock
    private WorkerFactory workerFactoryMock;

    @Mock
    private Worker workerMock;

    @Mock
    private SchedulePaymentActivitiesImpl activitiesImplMock;

    @Mock
    private TemporalConfigProperties configMock;

    @Mock
    private TemporalConfigProperties.WorkerConfig workerConfigMock;

    @Mock
    private TemporalOptionsHelper optionsHelperMock;

    private SchedulePaymentWorker schedulePaymentWorker;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(workerFactoryMock.newWorker(anyString())).thenReturn(workerMock);
        when(configMock.getWorkers()).thenReturn(Map.of(TemporalConstants.Workers.SCHEDULE_PAYMENT_WORKER, workerConfigMock));
        when(workerConfigMock.getTaskQueue()).thenReturn("testTaskQueue");

        schedulePaymentWorker = new SchedulePaymentWorker(workerFactoryMock, activitiesImplMock, configMock, optionsHelperMock);
    }

    @Test
    void run_shouldStartWorkerFactory() throws Exception {
        schedulePaymentWorker.run();

        // Verify task queue was created
        verify(workerFactoryMock).newWorker("testTaskQueue");

        // Verify activities and workflows are registered
        verify(workerMock).registerWorkflowImplementationFactory(eq(SchedulePaymentWorkflow.class), any());
        verify(workerMock).registerActivitiesImplementations(activitiesImplMock);

        // Verify worker factory started
        verify(workerFactoryMock).start();
    }

    @Test
    void registerWorkflowsAndActivities_shouldRegisterWorkflowsAndActivities() {
        ArgumentCaptor<Runnable> workflowFactoryCaptor = ArgumentCaptor.forClass(Runnable.class);
        Map<String, ActivityOptions> activityOptionsMap = Map.of("schedulePaymentActivities", mock(ActivityOptions.class));

        when(optionsHelperMock.createActivityOptions("schedulePaymentActivities", "v1")).thenReturn(activityOptionsMap.get("schedulePaymentActivities"));

        schedulePaymentWorker.registerWorkflowsAndActivities(workerMock);

        verify(workerMock).registerWorkflowImplementationFactory(eq(SchedulePaymentWorkflow.class), any());
        verify(workerMock).registerActivitiesImplementations(activitiesImplMock);
    }

    @Test
    void createActivityOptionsMap_shouldReturnCorrectOptionsMap() {
        ActivityOptions activityOptionsMock = mock(ActivityOptions.class);

        when(optionsHelperMock.createActivityOptions("schedulePaymentActivities", "v1"))
                .thenReturn(activityOptionsMock);
        when(optionsHelperMock.createActivityOptions("schedulePaymentActivitiesNoRetry", "v1"))
                .thenReturn(activityOptionsMock);

        Map<String, ActivityOptions> optionsMap = schedulePaymentWorker.createActivityOptionsMap();

        assertEquals(2, optionsMap.size());
        assertEquals(activityOptionsMock, optionsMap.get("schedulePaymentActivities"));
        assertEquals(activityOptionsMock, optionsMap.get("schedulePaymentActivitiesNoRetry"));
    }
}

