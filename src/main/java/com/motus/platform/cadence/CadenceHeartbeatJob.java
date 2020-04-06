package com.motus.platform.cadence;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


@DisallowConcurrentExecution
public class CadenceHeartbeatJob implements Job {

    private static Runnable runHeartbeatFunction;
    public static void setHeartbeatFunction(Runnable runHeartbeatFunction) {
        CadenceHeartbeatJob.runHeartbeatFunction = runHeartbeatFunction;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        runHeartbeatFunction.run();
    }
}
