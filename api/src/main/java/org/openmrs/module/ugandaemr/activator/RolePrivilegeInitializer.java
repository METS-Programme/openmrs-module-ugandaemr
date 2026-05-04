package org.openmrs.module.ugandaemr.activator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.dataexchange.DataImporter;

/**
 * Initializer that imports roles and privileges metadata.
 * This is a critical initialization step - failure will prevent module startup.
 */
public class RolePrivilegeInitializer implements Initializer {

    protected Log log = LogFactory.getLog(getClass());

    private static final String META_DATA_FILE_PATH = "metadata/";

    private final DataImporter dataImporter;

    public RolePrivilegeInitializer(DataImporter dataImporter) {
        this.dataImporter = dataImporter;
    }

    @Override
    public void started() {
        try {
            log.info("Starting roles and privileges metadata import");
            addRolePrivilege(META_DATA_FILE_PATH);
            log.info("Roles and privileges metadata import completed successfully");
        } catch (Exception e) {
            log.error("Failed to import roles and privileges metadata", e);
            throw new ModuleInitializationException("Failed to import roles and privileges metadata", e);
        }
    }

    @Override
    public void stopped() {
        // No cleanup needed
    }

    private void addRolePrivilege(String metaDataFilePath) {
        log.info("Start import of UgandaEMR Privileges");
        dataImporter.importData(metaDataFilePath + "Role_Privilege.xml");
        log.info("UgandaEMR Privileges Imported");
    }
}