package org.openmrs.module.ugandaemr.activator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.dataexchange.DataImporter;

/**
 * Initializer that imports order frequencies metadata.
 * This is a critical initialization step - failure will prevent module startup.
 */
public class OrderFrequenciesInitializer implements Initializer {

    protected Log log = LogFactory.getLog(getClass());

    private static final String META_DATA_FILE_PATH = "metadata/";

    private final DataImporter dataImporter;

    public OrderFrequenciesInitializer(DataImporter dataImporter) {
        this.dataImporter = dataImporter;
    }

    @Override
    public void started() {
        try {
            log.info("Starting order frequencies metadata import");
            addOrderFrequencies(META_DATA_FILE_PATH);
            log.info("Order frequencies metadata import completed successfully");
        } catch (Exception e) {
            log.error("Failed to import order frequencies metadata", e);
            throw new ModuleInitializationException("Failed to import order frequencies metadata", e);
        }
    }

    @Override
    public void stopped() {
        // No cleanup needed
    }

    private void addOrderFrequencies(String metaDataFilePath) {
        log.info("Start import of orderFrequencies related objects");
        dataImporter.importData(metaDataFilePath + "order_frequency.xml");
        log.info("orderFrequencies related objects Imported");
    }
}