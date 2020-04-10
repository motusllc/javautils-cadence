package com.motus.example.javautilscadence.workflow;

import com.motus.platform.cadence.WorkerStarterSuper;

public class WorkerStarter extends WorkerStarterSuper {
    @Override
    protected String getCadenceHost() {
        return "localhost";
    }

    @Override
    protected int getCadencePort() {
        return 7933;
    }

    @Override
    protected String getCadenceDomainName() {
        return "somedomain";
    }

    @Override
    protected String getCadenceDomainDescription() {
        return "Its a pretty great domain";
    }

    @Override
    protected String getCadenceTaskList() {
        return "sometasklist";
    }

    @Override
    protected Class[] getWorkflowTypes() {
        return new Class [] { GreatWorkflowImpl.class};
    }

    @Override
    protected Object[] getActivitiesObjs() {
        return new Object[] { new GreatActivitiesImpl() };
    }

    public void run() {
        start();
    }
}
