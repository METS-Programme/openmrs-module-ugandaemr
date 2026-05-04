/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the License); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * <p/>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p/>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.ugandaemr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.dataexchange.DataImporter;
import org.openmrs.module.ugandaemr.activator.*;
import org.openmrs.module.ugandaemr.api.UgandaEMRService;

/**
 * This class contains the logic that is run every time this module is either started or stopped.
 * <p>
 * Refactored to use the Initializer pattern for better separation of concerns, testability, and maintainability.
 * Each initialization step is now handled by a dedicated Initializer class.
 */
public class UgandaEMRActivator extends org.openmrs.module.BaseModuleActivator {

    protected Log log = LogFactory.getLog(getClass());

    private AdministrationService administrationService;
    private UgandaEMRService ugandaEMRService;

    /**
     * @see ModuleActivator#willRefreshContext()
     */
    public void willRefreshContext() {
        log.info("Refreshing ugandaemr Module");
    }

    /**
     * @see ModuleActivator#contextRefreshed()
     */
    public void contextRefreshed() {
        log.info("ugandaemr Module refreshed");
    }

    /**
     * @see ModuleActivator#willStart()
     */
    public void willStart() {
        log.info("Starting ugandaemr Module");
    }

    /**
     * @see ModuleActivator#started()
     */
    public void started() {
        try {
            initializeServices();

            // Phase 1: Apps Configuration (always runs first)
            executeInitializer(new AppsConfigurationInitializer());

            // Phase 2: Conditional Metadata Import
            if (shouldInitializeMetadata()) {
                executeMetadataInitializers();
                updateMetadataInitializationFlag();
            }

            // Phase 3: Form Initializers (always run)
            executeFormInitializers();

            // Phase 4: Core Configuration (always runs)
            executeInitializer(new CoreConfigurationInitializer());

            // Phase 5: Cleanup and Finalization (always run)
            executeInitializer(new DataIntegrityCleanupInitializer());
            executeInitializer(new PatientIdentifierInitializer());

            log.info("ugandaemr Module started");

        } catch (ModuleInitializationException e) {
            handleCriticalFailure(e);
        }
    }

    /**
     * Initialize service references from context
     */
    private void initializeServices() {
        administrationService = Context.getAdministrationService();
        ugandaEMRService = Context.getService(UgandaEMRService.class);
    }

    /**
     * Check if metadata should be initialized based on global property
     */
    private boolean shouldInitializeMetadata() {
        GlobalProperty initialiseMetaDataOnStart = administrationService
                .getGlobalPropertyObject("ugandaemr.initialiseMetadataOnStart");
        return "true".equals(initialiseMetaDataOnStart.getPropertyValue());
    }

    /**
     * Execute metadata import initializers in sequence
     * Following the same order as the original importInternalMetaData method
     */
    private void executeMetadataInitializers() {
        DataImporter dataImporter = Context.getRegisteredComponent("dataImporter", DataImporter.class);

        // Critical initializers - will stop module on failure
        // Following the order from original importInternalMetaData method:
        // 1. addConcepts
        executeInitializer(new ConceptsMetadataInitializer(dataImporter));

        // 2. attributeTypes
        executeInitializer(new AttributeTypesInitializer(dataImporter));

        // 3. addRolePrivilege
        executeInitializer(new RolePrivilegeInitializer(dataImporter));

        // 4. addVisitTypes
        executeNonCriticalInitializer(new VisitTypesInitializer(dataImporter));

        // 5. addRelationship
        executeInitializer(new RelationshipTypesInitializer(dataImporter));

        // 6. addOrderFrequencies
        executeInitializer(new OrderFrequenciesInitializer(dataImporter));

        // 7. addStockManagementData
        executeNonCriticalInitializer(new StockManagementInitializer(dataImporter));
    }

    /**
     * Execute form initializers from the service layer
     */
    private void executeFormInitializers() {
        for (Initializer initializer : ugandaEMRService.initialiseForms()) {
            executeInitializer(initializer);
        }
    }

    /**
     * Execute an initializer with critical error handling
     * Throws ModuleInitializationException on failure, which will stop the module
     */
    private void executeInitializer(Initializer initializer) {
        try {
            initializer.started();
        } catch (ModuleInitializationException e) {
            // Re-throw module initialization exceptions
            throw e;
        } catch (Exception e) {
            log.error("Initializer failed: " + initializer.getClass().getSimpleName(), e);
            throw new ModuleInitializationException(
                    "Failed to initialize: " + initializer.getClass().getSimpleName(), e);
        }
    }

    /**
     * Execute an initializer with non-critical error handling
     * Logs errors but does not throw exceptions, allowing module startup to continue
     */
    private void executeNonCriticalInitializer(Initializer initializer) {
        try {
            initializer.started();
        } catch (Exception e) {
            log.error("Non-critical initializer failed: " + initializer.getClass().getSimpleName() +
                    " (continuing module startup)", e);
            // Do not throw - allow module startup to continue
        }
    }

    /**
     * Update the global property to disable metadata initialization on next startup
     */
    private void updateMetadataInitializationFlag() {
        GlobalProperty initialiseMetaDataOnStart = administrationService
                .getGlobalPropertyObject("ugandaemr.initialiseMetadataOnStart");
        initialiseMetaDataOnStart.setPropertyValue("false");
        administrationService.saveGlobalProperty(initialiseMetaDataOnStart);
    }

    /**
     * Handle critical initialization failure by stopping the module
     */
    private void handleCriticalFailure(ModuleInitializationException e) {
        try {
            Module mod = ModuleFactory.getModuleById("ugandaemr");
            ModuleFactory.stopModule(mod);
        } catch (Exception stopException) {
            log.error("Failed to stop module after initialization error", stopException);
        }
        throw new RuntimeException("failed to setup the module ", e);
    }

    /**
     * @see ModuleActivator#willStop()
     */
    public void willStop() {
        log.info("Stopping ugandaemr Module");
    }

    /**
     * @see ModuleActivator#stopped()
     */
    public void stopped() {
        log.info("ugandaemr Module stopped");
    }
}