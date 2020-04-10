package com.motus.example.javautilscadence.workflow;

import com.uber.cadence.workflow.WorkflowMethod;

public interface GreatWorkflow {
    @WorkflowMethod
    void greatWorkflowMethod();
}
