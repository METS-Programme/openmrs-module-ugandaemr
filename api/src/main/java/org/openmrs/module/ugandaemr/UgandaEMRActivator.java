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
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleActivator;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.dataexchange.DataImporter;
import org.openmrs.module.ugandaemr.activator.Initializer;
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

            // install common metadata
            ugandaEMRService.installCommonMetadata();
            GlobalProperty initialiseMetaDataOnStart = administrationService.getGlobalPropertyObject("ugandaemr.initialiseMetadataOnStart");
            if (initialiseMetaDataOnStart.getPropertyValue().equals("true")) {
                DataImporter dataImporter = Context.getRegisteredComponent("dataImporter", DataImporter.class);
                // initialise forms and concepts and other metadata like privileges, personal attribute types
                importInternalMetaData(dataImporter);
                for (Initializer initializer : ugandaEMRService.initialiseForms()) {
                    initializer.started();
                }
                initialiseMetaDataOnStart.setPropertyValue("false");

                administrationService.saveGlobalProperty(initialiseMetaDataOnStart);
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

    private void importInternalMetaData(DataImporter dataImporter) {
        log.info("import  to Concept Table  Starting");
        log.info("import  to Concept Table  Starting");
        String metaDataFilePath = "metadata/";
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/Concept.xml");
        log.info("import to Concept Table  Successful");

        log.info("import  to Concept Name Table  Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/Concept_Name.xml");
        log.info("import to Concept Name Table  Successful");

        log.info("import  to Concept_Description Table  Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/Concept_Description.xml");
        log.info("import to Concept_Description Table  Successful");

        log.info("import  to Concept_Numeric Table  Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/Concept_Numeric.xml");
        log.info("import to Concept_Numeric Table  Successful");

        log.info("import  to Concept_Answer Table  Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/Concept_Answer.xml");
        log.info("import to Concept_Answer Table  Successful");

        log.info("import  to Concept_Set Table  Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/Concept_Set.xml");
        log.info("import to Concept_Set Table  Successful");

        log.info("import  to Concept_Reference Table  Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/Concept_Reference.xml");
        log.info("import to Concept_Reference Table  Successful");

        log.info("import  of  Concept Modifications Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/Concept_Modifications.xml");
        log.info("import to Concept Modifications Table  Successful");

        log.info("import  of  Drugs  Starting");
        dataImporter.importData(metaDataFilePath + "appointment.xml");
        log.info("import of Drugs  Successful");

        log.info("import  of  ICD 11 concepts  Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/icd_11/icd_11_import_concept.xml");
        log.info("import of ICD 11 concepts  Successful");

        log.info("import  of  ICD 11 concept_name Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/icd_11/icd_11_import_concept_name.xml");
        log.info("import of ICD 11 concept_name  Successful");

        log.info("import  of  ICD 11 concept_reference Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/icd_11/icd_11_import_concept_reference.xml");
        log.info("import of ICD 11 concept_reference  Successful");

        log.info("import  of  ICD 11 concept_map Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/icd_11/icd_11_import_concept_map.xml");
        log.info("import of ICD 11 concept_map  Successful");

        log.info("import  of  ICD 11 cause_of_death_set Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/cause_of_death_set.xml");
        log.info("import of ICD 11 cause_of_death_set  Successful");

        log.info("Move Non ICD Coded Diagnosis");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/icd_11/move_non_icd11-10-to-msc.xml");
        log.info("Move non coded ICD 11 Diagnosis");

        log.info("import  to Concept Table  Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/tools-2024/Concept.xml");
        log.info("import to Concept Table  Successful");

        log.info("import  to Concept Name Table  Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/tools-2024/Concept_Name.xml");
        log.info("import to Concept Name Table  Successful");

        log.info("import  to Concept_Description Table  Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/tools-2024/Concept_Description.xml");
        log.info("import to Concept_Description Table  Successful");

        log.info("import  to Concept_Numeric Table  Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/tools-2024/Concept_Numeric.xml");
        log.info("import to Concept_Numeric Table  Successful");

        log.info("import  to Concept_Answer Table  Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/tools-2024/Concept_Answer.xml");
        log.info("import to Concept_Answer Table  Successful");

        log.info("import  to Concept_Set Table  Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/tools-2024/Concept_Set.xml");
        log.info("import to Concept_Set Table  Successful");

        log.info("import  to Concept_Reference Table  Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/tools-2024/Concept_Reference.xml");
        log.info("import to Concept_Reference Table  Successful");

        log.info("import  of  Drugs  Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/Drug.xml");
        log.info("import of Drugs  Successful");

        log.info("Retire Meta data");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/retire_meta_data.xml");
        log.info("Retiring of meta data is Successful");

        log.info("Start import of person attributes");
        dataImporter.importData(metaDataFilePath + "Person_Attribute_Types.xml");
        log.info("Person Attributes imported");

        log.info("Start import of UgandaEMR Privileges");
        dataImporter.importData(metaDataFilePath + "Role_Privilege.xml");
        log.info("UgandaEMR Privileges Imported");

        log.info("Start import of UgandaEMR Visits");
        dataImporter.importData(metaDataFilePath + "VisitTypes.xml");
        log.info("UgandaEMR Visits Imported");

        log.info("Start import of UgandaEMR Relationship Types");
        dataImporter.importData(metaDataFilePath + "RelationshipTypes.xml");
        log.info("UgandaEMR Relationship Types Imported");

        log.info("Start import of Program related objects");
        dataImporter.importData(metaDataFilePath + "Programs.xml");
        log.info(" Program related objects Imported");

        log.info("Start import of orderFrequencies related objects");
        dataImporter.importData(metaDataFilePath + "order_frequency.xml");
        log.info(" orderFrequencies related objects Imported");

        log.info("Start import of stock item objects");
        dataImporter.importData(metaDataFilePath + "stockmanagement/stock_item.xml");
        dataImporter.importData(metaDataFilePath + "stockmanagement/stock_item_packaging_uom.xml");
        dataImporter.importData(metaDataFilePath + "stockmanagement/stock_item_uom.xml");
        log.info("stock item  objects Imported");
    }
}