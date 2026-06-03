package org.openmrs.module.ugandaemr.activator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.ugandaemr.api.UgandaEMRService;
import org.openmrs.api.context.Context;

/**
 * Initializer that handles core configuration setup.
 * This is a critical initialization step - failure will prevent module startup.
 * Wraps multiple service calls that should always run together.
 */
public class CoreConfigurationInitializer implements Initializer {

    protected Log log = LogFactory.getLog(getClass());

    private final UgandaEMRService ugandaEMRService;

    public CoreConfigurationInitializer() {
        this.ugandaEMRService = Context.getService(UgandaEMRService.class);
    }

    @Override
    public void started() {
        try {
            log.info("Starting core configuration setup");

            ugandaEMRService.installPatientFlags();
            log.info("Patient flags installed");

            ugandaEMRService.initializePrimaryIdentifierTypeMapping();
            log.info("Primary identifier type mapping initialized");

            ugandaEMRService.setHealthFacilityLocation();
            log.info("Health facility location set");

            ugandaEMRService.setFlagStatus();
            log.info("Flag status set");

            log.info("Core configuration setup completed successfully");
        } catch (Exception e) {
            log.error("Failed to complete core configuration setup", e);
            throw new ModuleInitializationException("Failed to complete core configuration setup", e);
        }
    }

    @Override
    public void stopped() {
        // No cleanup needed
    }
}