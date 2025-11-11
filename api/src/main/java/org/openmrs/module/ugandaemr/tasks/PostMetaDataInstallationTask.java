package org.openmrs.module.ugandaemr.tasks;

import org.openmrs.api.context.Context;
import org.openmrs.module.ugandaemr.api.UgandaEMRService;
import org.openmrs.scheduler.tasks.AbstractTask;

import java.util.concurrent.ExecutionException;

public class PostMetaDataInstallationTask extends AbstractTask {
    @Override
    public void execute() throws InterruptedException, ExecutionException {
        UgandaEMRService ugandaEMRService = Context.getService(UgandaEMRService.class);

        ugandaEMRService.installPatientFlags();

        // initialise primary Identifier
        ugandaEMRService.initializePrimaryIdentifierTypeMapping();

        // update the name of the default health center with that stored in the global property
        ugandaEMRService.setHealthFacilityLocation();

        ugandaEMRService.setFlagStatus();

        // cleanup liquibase change logs to enable installation of data integrity module
        ugandaEMRService.removeOldChangeLocksForDataIntegrityModule();
    }
}
