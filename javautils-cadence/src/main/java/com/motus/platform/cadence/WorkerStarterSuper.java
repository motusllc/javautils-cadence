package com.motus.platform.cadence;

import com.uber.cadence.DescribeDomainRequest;
import com.uber.cadence.client.WorkflowClient;
import com.uber.cadence.serviceclient.IWorkflowService;
import com.uber.cadence.serviceclient.WorkflowServiceTChannel;
import com.uber.cadence.worker.Worker;
import com.uber.cadence.worker.WorkerOptions;
import org.apache.commons.lang.StringUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class WorkerStarterSuper {
    /**
     * The logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerStarterSuper.class);

    /**
     * The current workflow service
     */
    private IWorkflowService workflowService;

    /**
     * The current worker factory
     */
    private Worker.Factory workerFactory;

    /**
     * The cadence host to connect to
     */
    protected abstract String getCadenceHost();

    /**
     * The port to talk to cadence on
     */
    protected abstract int getCadencePort();

    /**
     * The cadence domain name (will be registered if needed)
     */
    protected abstract String getCadenceDomainName();

    /**
     * The cadence domain description
     */
    protected abstract String getCadenceDomainDescription();

    /**
     * The task list name
     */
    protected abstract String getCadenceTaskList();

    /**
     * Workflow classes
     */
    protected abstract Class [] getWorkflowTypes();

    /**
     * Activity implementations
     */
    protected abstract Object [] getActivitiesObjs();

    /**
     * Worker options
     * @return
     */
    protected WorkerOptions getWorkerOptions() {
        return new WorkerOptions.Builder().build();
    }

    /**
     * The retention period in days for the domain (default 1)
     * @return
     */
    protected int getRetentionPeriodInDays() {
        return 1;
    }

    /**
     * The heartbeat check interval seconds (default 60)
     * @return
     */
    protected int getHeartbeatIntervalSeconds() {
        return 60;  //default 1 min, can override if necessary
    }

    /**
     * Here, have a nice workflow client
     * @return
     */
    protected WorkflowClient getWorkflowClient() {
        return WorkflowClient.newInstance(workflowService, getCadenceDomainName());
    }

    /**
     * Starts the workers and heartbeat function
     */
    protected void start() {
        // If the cadence config info isn't set, assume we're in an environment without it.
        if (StringUtils.isEmpty(getCadenceHost())) {
            LOGGER.warn("No cadence server configured");
            return;
        }

        workflowService = new WorkflowServiceTChannel(getCadenceHost(), getCadencePort());

        // Make sure the domain is registered
        Util.registerDomainIfNeeded(workflowService, getCadenceDomainName(), getCadenceDomainDescription(), getRetentionPeriodInDays(), LOGGER);

        registerWorkers();

        //now setup the heartbeat...
        CadenceHeartbeatJob.setHeartbeatFunction(this::runHeartbeat);

        Trigger heartbeatTrigger = TriggerBuilder.newTrigger()
                .startNow()
                .withSchedule(
                       SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(getHeartbeatIntervalSeconds())
                       .repeatForever())
                .build();

        JobDetail heartbeatJob = JobBuilder.newJob(CadenceHeartbeatJob.class)
                .build();

        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        try {
            Scheduler scheduler = schedulerFactory.getScheduler();
            scheduler.scheduleJob(heartbeatJob, heartbeatTrigger);
            scheduler.start();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    private void registerWorkers() {
        LOGGER.info("registerWorkers has been called");
        workerFactory = new Worker.Factory(workflowService, getCadenceDomainName());
        Worker worker = workerFactory.newWorker(getCadenceTaskList(), getWorkerOptions());
        worker.registerWorkflowImplementationTypes(getWorkflowTypes());
        worker.registerActivitiesImplementations(getActivitiesObjs());
        workerFactory.start();
    }

    private void resetFactory() {
        LOGGER.error("Could not describe domain - shutting down and restarting factory workers");
        workerFactory.shutdown();
        workflowService = new WorkflowServiceTChannel(getCadenceHost(), getCadencePort());
        registerWorkers();
    }

    private void runHeartbeat() {
        DescribeDomainRequest request = new DescribeDomainRequest();
        try {
            request.setName(getCadenceDomainName());
            workflowService.DescribeDomain(request);
            LOGGER.debug("domain heartbeat call success");
        } catch (Exception e) {
            resetFactory();
        }
    }
}
