package org.openmrs.module.ugandaemr.activator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.ugandaemr.api.UgandaEMRService;
import org.openmrs.api.context.Context;

/**
 * Initializer that generates OpenMRS identifiers for patients without them.
 * This is a non-critical initialization step - failures are logged but do not prevent module startup.
 */
public class PatientIdentifierInitializer implements Initializer {

    protected Log log = LogFactory.getLog(getClass());

    private final UgandaEMRService ugandaEMRService;

    public PatientIdentifierInitializer() {
        this.ugandaEMRService = Context.getService(UgandaEMRService.class);
    }

    @Override
    public void started() {
        try {
            log.info("Starting patient identifier generation");
            ugandaEMRService.generateOpenMRSIdentifierForPatientsWithout();
            log.info("Patient identifier generation completed successfully");
        } catch (Exception e) {
            log.error("Failed to generate patient identifiers (non-critical, continuing)", e);
            // Non-critical - do not throw exception
        }
    }

    @Override
    public void stopped() {
        // No cleanup needed
    }
}