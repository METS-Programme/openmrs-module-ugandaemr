package org.openmrs.module.ugandaemr.activator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.ugandaemr.api.UgandaEMRService;
import org.openmrs.api.context.Context;

/**
 * Initializer that enables/disables core apps functionality.
 * This should always run first during module startup.
 */
public class AppsConfigurationInitializer implements Initializer {

    protected Log log = LogFactory.getLog(getClass());

    private final UgandaEMRService ugandaEMRService;

    public AppsConfigurationInitializer() {
        this.ugandaEMRService = Context.getService(UgandaEMRService.class);
    }

    @Override
    public void started() {
        log.info("Configuring apps for UgandaEMR");
        try {
            ugandaEMRService.disableEnableAPPS();
            log.info("Apps configuration completed successfully");
        } catch (Exception e) {
            log.error("Failed to configure apps", e);
            throw new ModuleInitializationException("Failed to configure apps", e);
        }
    }

    @Override
    public void stopped() {
        // No cleanup needed
    }
}