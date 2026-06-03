package org.openmrs.module.ugandaemr.activator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.ugandaemr.api.UgandaEMRService;
import org.openmrs.api.context.Context;

/**
 * Initializer that cleans up old Liquibase change locks to enable installation of data integrity module.
 * This is a non-critical initialization step - failures are logged but do not prevent module startup.
 */
public class DataIntegrityCleanupInitializer implements Initializer {

    protected Log log = LogFactory.getLog(getClass());

    private final UgandaEMRService ugandaEMRService;

    public DataIntegrityCleanupInitializer() {
        this.ugandaEMRService = Context.getService(UgandaEMRService.class);
    }

    @Override
    public void started() {
        try {
            log.info("Starting data integrity cleanup");
            ugandaEMRService.removeOldChangeLocksForDataIntegrityModule();
            log.info("Data integrity cleanup completed successfully");
        } catch (Exception e) {
            log.error("Failed to cleanup data integrity locks (non-critical, continuing)", e);
            // Non-critical - do not throw exception
        }
    }

    @Override
    public void stopped() {
        // No cleanup needed
    }
}