/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
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
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleActivator;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.ugandaemr.api.UgandaEMRService;

/**
 * This class contains the logic that is run every time this module is either started or stopped.
 * <p>
 * TODO: Refactor the whole class to use initializers like
 */
public class UgandaEMRActivator extends org.openmrs.module.BaseModuleActivator {

    protected Log log = LogFactory.getLog(getClass());

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
        AdministrationService administrationService = Context.getAdministrationService();
        UgandaEMRService ugandaEMRService = Context.getService(UgandaEMRService.class);

        try {
            // enable disable apps of in coreapps
            ugandaEMRService.disableEnableAPPS();

            //ugandaEMRService.copyFilesToApplicationDataDirectory("metadata","metadata");
            //ugandaEMRService.copyFilesToApplicationDataDirectory("jsonforms","jsonforms");
            //ugandaEMRService.copyFilesToApplicationDataDirectory("metadata","metadata");

            String initialiseMetaDataOnStart=administrationService.getGlobalProperty("ugandaemr.initialiseMetadataOnStart");
            if(initialiseMetaDataOnStart.equals("true")) {
                // initialise forms and concepts and other metadata like privileges, personal attribute types
                ugandaEMRService.initializeMetaData();
            }

            // initialise primary Identifier
            ugandaEMRService.initializePrimaryIdentifierTypeMapping();

            // update the name of the default health center with that stored in the global property
            ugandaEMRService.setHealthFacilityLocation();

            ugandaEMRService.setFlagStatus();

            // cleanup liquibase change logs to enable installation of data integrity module
            ugandaEMRService.removeOldChangeLocksForDataIntegrityModule();

            // generate OpenMRS ID for patients without the identifier
            ugandaEMRService.generateOpenMRSIdentifierForPatientsWithout();
            log.info("ugandaemr Module started");

        } catch (Exception e) {
            Module mod = ModuleFactory.getModuleById("ugandaemr");
            ModuleFactory.stopModule(mod);
            throw new RuntimeException("failed to setup the module ", e);
        }
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