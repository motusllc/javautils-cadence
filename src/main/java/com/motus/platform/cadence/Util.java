package com.motus.platform.cadence;

import com.uber.cadence.*;
import com.uber.cadence.serviceclient.IWorkflowService;
import com.uber.cadence.serviceclient.WorkflowServiceTChannel;
import org.apache.thrift.TException;
import org.slf4j.Logger;

public class Util {

    /**
     * Register a cadence domain
     * @param cadenceHost
     * @param cadencePort
     * @param cadenceDomainName
     * @param cadenceDomainDescription
     * @param retentionPeriodInDays
     * @param logger
     * @return
     */
    public static boolean registerDomainIfNeeded(String cadenceHost, int cadencePort, String cadenceDomainName, String cadenceDomainDescription, int retentionPeriodInDays, Logger logger) {
        IWorkflowService cadenceService = new WorkflowServiceTChannel(cadenceHost, cadencePort);
        return registerDomainIfNeeded(cadenceService, cadenceDomainName, cadenceDomainDescription, retentionPeriodInDays, logger);
    }

    /**
     * Register a cadence domain
     * @param cadenceService
     * @param cadenceDomainName
     * @param cadenceDomainDescription
     * @param retentionPeriodInDays
     * @param logger
     * @return
     */
    public static boolean registerDomainIfNeeded(IWorkflowService cadenceService, String cadenceDomainName, String cadenceDomainDescription, int retentionPeriodInDays, Logger logger) {
        RegisterDomainRequest request = new RegisterDomainRequest();
        request.setDescription(cadenceDomainDescription);
        request.setName(cadenceDomainName);

        request.setWorkflowExecutionRetentionPeriodInDays(retentionPeriodInDays);
        String successString = null;
        try {
            cadenceService.RegisterDomain(request);
            successString = "Successfully registered domain \"" + cadenceDomainName + "\"";
        } catch (DomainAlreadyExistsError e) {
            successString = "Domain \"" + cadenceDomainName + "\" has already been registered.";
        } catch (TException e) {
            e.printStackTrace();
            logger.error("Failed to register domain \"" + cadenceDomainName + "\":  " + e.getMessage(), e);
        }

        boolean result = successString != null;
        if (successString != null) {
            logger.info(successString);
        } else {
            result = false;
        }

        return result;
    }
}
