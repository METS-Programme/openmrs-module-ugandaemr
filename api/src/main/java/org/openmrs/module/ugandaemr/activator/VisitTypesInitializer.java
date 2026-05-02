package org.openmrs.module.ugandaemr.activator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.dataexchange.DataImporter;

/**
 * Initializer that imports visit types metadata.
 * This is a non-critical initialization step - failures are logged but do not prevent module startup.
 */
public class VisitTypesInitializer implements Initializer {

    protected Log log = LogFactory.getLog(getClass());

    private static final String META_DATA_FILE_PATH = "metadata/";

    private final DataImporter dataImporter;

    public VisitTypesInitializer(DataImporter dataImporter) {
        this.dataImporter = dataImporter;
    }

    @Override
    public void started() {
        try {
            log.info("Starting visit types metadata import");
            addVisitTypes(META_DATA_FILE_PATH);
            log.info("Visit types metadata import completed successfully");
        } catch (Exception e) {
            log.error("Failed to import visit types metadata (non-critical, continuing)", e);
            // Non-critical - do not throw exception
        }
    }

    @Override
    public void stopped() {
        // No cleanup needed
    }

    private void addVisitTypes(String metaDataFilePath) {
        log.info("Start import of UgandaEMR Visits");
        dataImporter.importData(metaDataFilePath + "VisitTypes.xml");
        log.info("UgandaEMR Visits Imported");
    }
}