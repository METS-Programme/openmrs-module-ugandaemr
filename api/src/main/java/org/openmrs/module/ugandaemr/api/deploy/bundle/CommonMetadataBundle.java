package org.openmrs.module.ugandaemr.api.deploy.bundle;

import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.descriptor.LocationDescriptor;
import org.openmrs.module.ugandaemr.metadata.core.*;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.encounterRole;

import org.openmrs.module.ugandaemr.metadata.core.location.*;
import org.springframework.stereotype.Component;

/**
 * Installs the common metadata
 */
@Component
public class CommonMetadataBundle extends AbstractMetadataBundle {

    /**
     * @see org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle#install()
     */
    public void install() throws Exception {
        // install the patient identifier types
        log.info("Installing PatientIdentifierTypes");
        install(PatientIdentifierTypes.HIV_CARE_NUMBER);
        install(PatientIdentifierTypes.OLD_OPENMRS_IDENTIFICATION_NUMBER);
        install(PatientIdentifierTypes.OPENMRS_ID);
        install(PatientIdentifierTypes.OPENMRS_IDENTIFICATION_NUMBER);
        install(PatientIdentifierTypes.EXPOSED_INFANT_NUMBER);
        install(PatientIdentifierTypes.ANC_NUMBER);
        install(PatientIdentifierTypes.PNC_NUMBER);
        install(PatientIdentifierTypes.IPD_NUMBER);
        install(PatientIdentifierTypes.NATIONAL_ID);
        install(PatientIdentifierTypes.ART_PATIENT_NUMBER);
        install(PatientIdentifierTypes.RESEARCH_PATIENT_ID);
        install(PatientIdentifierTypes.SMC_CLIENT_NUMBER);
        install(PatientIdentifierTypes.REFUGEE_IDENTIFICATION_NUMBER);
        install(PatientIdentifierTypes.PATIENT_IUC_HEALTH_ID);
        install(PatientIdentifierTypes.PATIENT_ORGANIZATION_ID);
        install(PatientIdentifierTypes.PATIENT_OPD_IDENTIFICATION_NUMBER);
        install(PatientIdentifierTypes.DISTRICT_REG_TB_NUMBER);
        install(PatientIdentifierTypes.NHPI);
        install(PatientIdentifierTypes.PASSPORT);
        log.info("Patient IdentifierTypes installed");

        // install person attribute types
        log.info("Installing PatientAttributeTypes");
        install(PersonAttributeTypes.MARITAL_STATUS);
        install(PersonAttributeTypes.HEALTH_CENTER);
        install(PersonAttributeTypes.HEALTH_FACILITY_DISTRICT);
        install(PersonAttributeTypes.TELEPHONE_NUMBER_2);
        install(PersonAttributeTypes.TELEPHONE_NUMBER_3);
        install(PersonAttributeTypes.OCCUPATION);
        install(PersonAttributeTypes.NATIONALITY);
        install(PersonAttributeTypes.EMAIL_ADDRESS);
        install(PersonAttributeTypes.EDUCATION_LEVEL);
        install(PersonAttributeTypes.RACE);
        install(PersonAttributeTypes.BIRTHPLACE);
        install(PersonAttributeTypes.CITIZENSHIP);
        install(PersonAttributeTypes.MOTHERS_NAME);
        install(PersonAttributeTypes.TELEPHONE_NUMBER);
        install(PersonAttributeTypes.UNKNOWN_PATIENT);
        install(PersonAttributeTypes.TEST_PATIENT);
        install(PersonAttributeTypes.FINGERPRINT);
        install(PersonAttributeTypes.FATHERS_NAME);
        install(PersonAttributeTypes.CARE_GIVER_NAME);
        install(PersonAttributeTypes.CARE_GIVER_TELEPHONE_NUMBER);
        install(PersonAttributeTypes.COMMON_NAME);
        install(PersonAttributeTypes.LAND_MARK_FEATURE);
        install(PersonAttributeTypes.DIRECTIONS_TO_PATIENT_HOME);
        install(PersonAttributeTypes.EMAIL);
        log.info("Person AttributeTypes installed");


        //Install Encounter Type
        log.info("Installing EncounterTypes");
        install(EncounterTypes.ART_SUMMARY_ENCOUNTER_TYPE);
        install(EncounterTypes.ART_ENCOUNTER_TYPE);
        install(EncounterTypes.PNC_ENCOUNTER_TYPE);
        install(EncounterTypes.OPD_ENCOUNTER);
        install(EncounterTypes.TB_SUMMARY_ENCOUNTER);
        install(EncounterTypes.TB_FOLLOWUP_ENCOUNTER);
        install(EncounterTypes.VIRAL_LOAD_NON_SUPPRESSED);
        install(EncounterTypes.APPOINTMENT_FOLLOW_UP);
        install(EncounterTypes.TRIAGE);
        install(EncounterTypes.MEDICATION_DISPENSE);
        install(EncounterTypes.MISSED_APPOINTMENT_TRACKING);
        install(EncounterTypes.TRANSFER_IN);
        install(EncounterTypes.TRANSFER_OUT);
        install(EncounterTypes.DR_TB_SUMMARY_ENCOUNTER);
        install(EncounterTypes.DR_TB_FOLLOWUP_ENCOUNTER);
        install(EncounterTypes.ART_REGIMEN_CHANGE);
        install(EncounterTypes.COVID19_ENROLLMENT);
        install(EncounterTypes.COVID19_FOLLOWUP);
        install(EncounterTypes.COVID19_DISCHARGE);
        install(EncounterTypes.COVID19_POSTMORTEM);
        install(EncounterTypes.COVID19_REFERRAL);
        install(EncounterTypes.EMERGENCY_ART_SERVICE);
        install(EncounterTypes.CACX_SCREENING_LOG);
        install(EncounterTypes.CACX_TREATMENT_REGISTER);
        install(EncounterTypes.COVID19_VACCINATION_TRACKING);
        install(EncounterTypes.SMS_ENROLLMENT);
        install(EncounterTypes.FAMILY_PLANNING_ENCOUNTER);
        install(EncounterTypes.NEW_BORN_INPATIENT_ENCOUNTER);
        install(EncounterTypes.IN_PATIENT_ENCOUNTER);
        install(EncounterTypes.CHILD_HEALTH_ENCOUNTER);
        install(EncounterTypes.SMC_FOLLOWUP);
        install(EncounterTypes.LAB_REQUEST_ENCOUNTER);
        install(EncounterTypes.INTER_FACILITY_LINKAGE_ENCOUNTER);
        install(EncounterTypes.MOBILITY_SCREENING);
        install(EncounterTypes.FAMILY_TRACKING);
        install(EncounterTypes.DEATH_NOTIFICATION);
        install(EncounterTypes.REGISTRATION);
        install(EncounterTypes.MEDICATION_ORDER);
        install(EncounterTypes.TB_SCREENING_ENCOUNTER);
        install(EncounterTypes.MONKEY_POX_SCREENING_ENCOUNTER);
        install(EncounterTypes.PROCEDURE_RESULTS_ENCOUNTER);
        install(EncounterTypes.HIV_SELF_TESTING_ENCOUNTER);
        install(EncounterTypes.HTS_CONTACT_TRACING_ENCOUNTER);
        install(EncounterTypes.ATTACHMENT_UPLOAD);
        install(EncounterTypes.DISCHARGE);
        install(EncounterTypes.ADMISSION);
        install(EncounterTypes.VISIT_NOTE);
        install(EncounterTypes.CHECK_OUT);
        install(EncounterTypes.CHECK_IN);
        install(EncounterTypes.TRANSFER);
        install(EncounterTypes.VITALS);
        install(EncounterTypes.LAB_ENCOUNTER);
        install(EncounterTypes.SMC_ENCOUNTER);
        install(EncounterTypes.EID_CARD_ENCOUNTER);
        install(EncounterTypes.ART_HEALTH_EDUCATION);
        install(EncounterTypes.EID_CARD_SUMMARY);
        install(EncounterTypes.MATERNITY_ENCOUNTER);
        install(EncounterTypes.EARLY_INFANT_DIAGNOSIS_REQUEST);


        // Install Encounter Role
        install(encounterRole(EncounterRoles.ASSISTANT_CIRCUMCISER_NAME, EncounterRoles.ASSISTANT_CIRCUMCISER_DESCRIPTION, EncounterRoles.ASSISTANT_CIRCUMCISER_UUID));
        install(encounterRole(EncounterRoles.PHARMACIST_NAME, EncounterRoles.PHARMACIST_DESCRIPTION, EncounterRoles.PHARMACIST_UUID));
    }

    private void installLocation(LocationDescriptor locationDescriptor) {
        Location location = Context.getLocationService().getLocationByUuid(locationDescriptor.uuid());
        if (location == null) {
            install(locationDescriptor);
        }
    }
}