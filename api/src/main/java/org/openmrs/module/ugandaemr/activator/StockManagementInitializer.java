package org.openmrs.module.ugandaemr.activator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.dataexchange.DataImporter;

/**
 * Initializer that imports stock management metadata.
 * This is a non-critical initialization step - failures are logged but do not prevent module startup.
 */
public class StockManagementInitializer implements Initializer {

    protected Log log = LogFactory.getLog(getClass());

    private static final String META_DATA_FILE_PATH = "metadata/";

    private final DataImporter dataImporter;

    public StockManagementInitializer(DataImporter dataImporter) {
        this.dataImporter = dataImporter;
    }

    @Override
    public void started() {
        try {
            log.info("Starting stock management metadata import");
            addStockManagementData(META_DATA_FILE_PATH);
            log.info("Stock management metadata import completed successfully");
        } catch (Exception e) {
            log.error("Failed to import stock management metadata (non-critical, continuing)", e);
            // Non-critical - do not throw exception
        }
    }

    @Override
    public void stopped() {
        // No cleanup needed
    }

    private void addStockManagementData(String metaDataFilePath) {
        log.info("Start import of stock item objects");
        dataImporter.importData(metaDataFilePath + "stockmanagement/stock_item.xml");
        dataImporter.importData(metaDataFilePath + "stockmanagement/stock_item_packaging_uom.xml");
        dataImporter.importData(metaDataFilePath + "stockmanagement/stock_item_uom.xml");
        log.info("stock item objects Imported");
    }
}