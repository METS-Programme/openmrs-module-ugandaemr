package org.openmrs.module.ugandaemr.activator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.dataexchange.DataImporter;

/**
 * Initializer that imports relationship types metadata.
 * This is a critical initialization step - failure will prevent module startup.
 */
public class RelationshipTypesInitializer implements Initializer {

    protected Log log = LogFactory.getLog(getClass());

    private static final String META_DATA_FILE_PATH = "metadata/";

    private final DataImporter dataImporter;

    public RelationshipTypesInitializer(DataImporter dataImporter) {
        this.dataImporter = dataImporter;
    }

    @Override
    public void started() {
        try {
            log.info("Starting relationship types metadata import");
            addRelationshipTypes(META_DATA_FILE_PATH);
            log.info("Relationship types metadata import completed successfully");
        } catch (Exception e) {
            log.error("Failed to import relationship types metadata", e);
            throw new ModuleInitializationException("Failed to import relationship types metadata", e);
        }
    }

    @Override
    public void stopped() {
        // No cleanup needed
    }

    private void addRelationshipTypes(String metaDataFilePath) {
        log.info("Start import of UgandaEMR Relationship Types");
        dataImporter.importData(metaDataFilePath + "RelationshipTypes.xml");
        log.info("UgandaEMR Relationship Types Imported");
    }
}