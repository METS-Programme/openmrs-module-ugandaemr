package org.openmrs.module.ugandaemr.activator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.dataexchange.DataImporter;

/**
 * Initializer that imports all concepts and drugs metadata.
 * This is a critical initialization step - failure will prevent module startup.
 */
public class ConceptsMetadataInitializer implements Initializer {

    protected Log log = LogFactory.getLog(getClass());

    private static final String META_DATA_FILE_PATH = "metadata/";

    private final DataImporter dataImporter;

    public ConceptsMetadataInitializer(DataImporter dataImporter) {
        this.dataImporter = dataImporter;
    }

    @Override
    public void started() {
        try {
            log.info("Starting concepts and drugs metadata import");
            addConcepts(META_DATA_FILE_PATH);
            log.info("Concepts and drugs metadata import completed successfully");
        } catch (Exception e) {
            log.error("Failed to import concepts and drugs metadata", e);
            throw new ModuleInitializationException("Failed to import concepts and drugs metadata", e);
        }
    }

    @Override
    public void stopped() {
        // No cleanup needed
    }

    private void addConcepts(String metaDataFilePath) {
        log.info("import to Concept Table Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/Concept.xml");
        log.info("import to Concept Table Successful");

        log.info("import to Concept Name Table Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/Concept_Name.xml");
        log.info("import to Concept Name Table Successful");

        log.info("import to Concept_Description Table Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/Concept_Description.xml");
        log.info("import to Concept_Description Table Successful");

        log.info("import to Concept_Numeric Table Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/Concept_Numeric.xml");
        log.info("import to Concept_Numeric Table Successful");

        log.info("import to Concept_Answer Table Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/Concept_Answer.xml");
        log.info("import to Concept_Answer Table Successful");

        log.info("import to Concept_Set Table Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/Concept_Set.xml");
        log.info("import to Concept_Set Table Successful");

        log.info("import to Concept_Reference Table Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/Concept_Reference.xml");
        log.info("import to Concept_Reference Table Successful");

        log.info("import of Concept Modifications Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/Concept_Modifications.xml");
        log.info("import to Concept Modifications Table Successful");

        log.info("import of ICD 11 concepts Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/icd_11/icd_11_import_concept.xml");
        log.info("import of ICD 11 concepts Successful");

        log.info("import of ICD 11 concept_name Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/icd_11/icd_11_import_concept_name.xml");
        log.info("import of ICD 11 concept_name Successful");

        log.info("import of ICD 11 concept_reference Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/icd_11/icd_11_import_concept_reference.xml");
        log.info("import of ICD 11 concept_reference Successful");

        log.info("import of ICD 11 concept_map Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/icd_11/icd_11_import_concept_map.xml");
        log.info("import of ICD 11 concept_map Successful");

        log.info("import of ICD 11 cause_of_death_set Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/cause_of_death_set.xml");
        log.info("import of ICD 11 cause_of_death_set Successful");

        log.info("Move Non ICD Coded Diagnosis");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/icd_11/move_non_icd11-10-to-msc.xml");
        log.info("Move non coded ICD 11 Diagnosis");

        log.info("import to Concept Table Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/tools-2024/Concept.xml");
        log.info("import to Concept Table Successful");

        log.info("import to Concept Name Table Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/tools-2024/Concept_Name.xml");
        log.info("import to Concept Name Table Successful");

        log.info("import to Concept_Description Table Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/tools-2024/Concept_Description.xml");
        log.info("import to Concept_Description Table Successful");

        log.info("import to Concept_Numeric Table Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/tools-2024/Concept_Numeric.xml");
        log.info("import to Concept_Numeric Table Successful");

        log.info("import to Concept_Answer Table Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/tools-2024/Concept_Answer.xml");
        log.info("import to Concept_Answer Table Successful");

        log.info("import to Concept_Set Table Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/tools-2024/Concept_Set.xml");
        log.info("import to Concept_Set Table Successful");

        log.info("import to Concept_Reference Table Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/tools-2024/Concept_Reference.xml");
        log.info("import to Concept_Reference Table Successful");

        log.info("import to Concept_Reference_Range Table Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/Concept_Reference_Range.xml");
        log.info("import to Concept_Reference_Range Table Successful");

        log.info("import to Concept_Reference_Range Table Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/tools-2024/Concept_Reference_Range.xml");
        log.info("import to Concept_Reference_Range Table Successful");

        log.info("import of Drugs Starting");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/Drug.xml");
        log.info("import of Drugs Successful");

        log.info("Retire Meta data");
        dataImporter.importData(metaDataFilePath + "concepts_and_drugs/retire_meta_data.xml");
        log.info("Retiring of meta data is Successful");
    }
}