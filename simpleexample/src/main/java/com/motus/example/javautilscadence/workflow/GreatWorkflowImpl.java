package com.motus.example.javautilscadence.workflow;

import com.uber.cadence.activity.ActivityOptions;
import com.uber.cadence.common.RetryOptions;
import com.uber.cadence.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.Date;

public class GreatWorkflowImpl implements GreatWorkflow {

    private static Logger logger = Workflow.getLogger(GreatWorkflowImpl.class);

    private GreatActivities greatActivities;

    public GreatWorkflowImpl() {
        greatActivities = Workflow.newActivityStub(GreatActivities.class,
                new ActivityOptions.Builder()
                        .setScheduleToCloseTimeout(Duration.ofSeconds(30))
                        .setRetryOptions(new RetryOptions.Builder()
                                .setMaximumAttempts(3)
                                .setInitialInterval(Duration.ofSeconds(30))
                                .build())
                        .build());
    }

    @Override
    public void greatWorkflowMethod() {
        logger.info("greatWorkflowMethod has been called");
        Date aDate = greatActivities.getADateForSomeReason();
        logger.info("Got a date=" + aDate);
        greatActivities.printSomethingToConsole("Well, its something...");
    }

}
