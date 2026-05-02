package org.openmrs.module.ugandaemr.activator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.dataexchange.DataImporter;

/**
 * Initializer that imports person attribute types metadata.
 * This is a critical initialization step - failure will prevent module startup.
 */
public class AttributeTypesInitializer implements Initializer {

    protected Log log = LogFactory.getLog(getClass());

    private static final String META_DATA_FILE_PATH = "metadata/";

    private final DataImporter dataImporter;

    public AttributeTypesInitializer(DataImporter dataImporter) {
        this.dataImporter = dataImporter;
    }

    @Override
    public void started() {
        try {
            log.info("Starting person attribute types metadata import");
            addAttributeTypes(META_DATA_FILE_PATH);
            log.info("Person attribute types metadata import completed successfully");
        } catch (Exception e) {
            log.error("Failed to import person attribute types metadata", e);
            throw new ModuleInitializationException("Failed to import person attribute types metadata", e);
        }
    }

    @Override
    public void stopped() {
        // No cleanup needed
    }

    private void addAttributeTypes(String metaDataFilePath) {
        log.info("Start import of person attributes");
        dataImporter.importData(metaDataFilePath + "Person_Attribute_Types.xml");
        log.info("Person Attributes imported");
    }
}