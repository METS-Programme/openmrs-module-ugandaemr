package org.openmrs.module.ugandaemr.metadata.core;

import org.openmrs.module.metadatadeploy.descriptor.EncounterTypeDescriptor;

/**
 * Created by lubwamasamuel on 18/10/16.
 */
public class EncounterTypes {

    public static EncounterTypeDescriptor ART_SUMMARY_ENCOUNTER_TYPE = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "ART Card - Summary";
        }

        @Override
        public String description() {
            return "Outpatient Adult Initial Visit";
        }

        public String uuid() {
            return "8d5b27bc-c2cc-11de-8d13-0010c6dffd0f";
        }
    };

    public static EncounterTypeDescriptor ART_ENCOUNTER_TYPE = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "ART Card - Encounter";
        }

        @Override
        public String description() {
            return "Outpatient Adult & Child Return Visit";
        }

        public String uuid() {
            return "8d5b2be0-c2cc-11de-8d13-0010c6dffd0f";
        }
    };

    public static EncounterTypeDescriptor PNC_ENCOUNTER_TYPE = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "PNC - Encounter";
        }

        @Override
        public String description() {
            return "An encounter when a patient gets PNC services";
        }

        public String uuid() {
            return "fa6f3ff5-b784-43fb-ab35-a08ab7dbf074";
        }
    };

    public static EncounterTypeDescriptor OPD_ENCOUNTER = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "OPD Encounter";
        }

        @Override
        public String description() {
            return "Outpatient Clinical Ecnounter";
        }

        public String uuid() {
            return "ee4780f5-b5eb-423b-932f-00b5879df5ab";
        }
    };

    public static EncounterTypeDescriptor SMC_FOLLOWUP = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "SMC Follow up Encounter";
        }

        @Override
        public String description() {
            return "SMS Follow up Encounter";
        }

        public String uuid() {
            return "d0f9e0b7-f336-43bd-bf50-0a7243857fa6";
        }
    };

    public static EncounterTypeDescriptor TB_SUMMARY_ENCOUNTER = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "TB Summary (Enrollment)";
        }

        @Override
        public String description() {
            return "An encounter for the initial visit to the TB clinic";
        }

        public String uuid() {
            return "334bf97e-28e2-4a27-8727-a5ce31c7cd66";
        }
    };

    public static EncounterTypeDescriptor TB_FOLLOWUP_ENCOUNTER = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "TB Encounter (Followup)";
        }

        @Override
        public String description() {
            return "An encounter for a return visit to the TB clinic";
        }

        public String uuid() {
            return "455bad1f-5e97-4ee9-9558-ff1df8808732";
        }
    };


    public static EncounterTypeDescriptor DR_TB_SUMMARY_ENCOUNTER = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "DR TB Summary (Enrollment)";
        }

        @Override
        public String description() {
            return "An encounter for the initial visit to the Drug Resistance TB Program";
        }

        public String uuid() {
            return "0271ee3d-f274-49d1-b376-c842f075413f";
        }
    };

    public static EncounterTypeDescriptor DR_TB_FOLLOWUP_ENCOUNTER = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "DR TB Encounter (Followup)";
        }

        @Override
        public String description() {
            return "An encounter for a return visit to the Drug Resistance TB Program";
        }

        public String uuid() {
            return "41f8609d-e13b-4dff-8379-47ac5876512e";
        }
    };


    public static EncounterTypeDescriptor VIRAL_LOAD_NON_SUPPRESSED  = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "Viral Load Non Suppressed Encounter";
        }

        @Override
        public String description() {
            return "Viral Load Non Suppressed Follow up";
        }

        public String uuid() {
            return "38cb2232-30fc-4b1f-8df1-47c795771ee9";
        }
    };

    public static EncounterTypeDescriptor APPOINTMENT_FOLLOW_UP = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "Appointment Follow-up ";
        }

        @Override
        public String description() {
            return "Followup actions for patients especially after missing a facility visit";
        }

        public String uuid() {
            return "dc551efc-024d-4c40-aeb8-2147c4033778";
        }
    };

    public static EncounterTypeDescriptor TRIAGE = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "Triage";
        }

        @Override
        public String description() {
            return "This is a form to capture information on triage. It include Vitals, global security indicators etc....";
        }

        public String uuid() {
            return "0f1ec66d-61db-4575-8248-94e10a88178f";
        }
    };

    public static EncounterTypeDescriptor MEDICATION_DISPENSE = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "Medication Dispense";
        }

        @Override
        public String description() {
            return "This encounter type is for dispensing of medication at facility";
        }

        public String uuid() {
            return "22902411-19c1-4a02-b19a-bf1a9c24fd51";
        }
    };

    public static EncounterTypeDescriptor MISSED_APPOINTMENT_TRACKING = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "Missed Appointment Tracking";
        }

        @Override
        public String description() {
            return "This encounter type is for tracking followup for missed appointments";
        }

        public String uuid() {
            return "791faefd-36b8-482f-ab78-20c297b03851";
        }
    };

    public static EncounterTypeDescriptor TRANSFER_IN = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "Transfer In";
        }

        @Override
        public String description() {
            return "Transfer in encounter";
        }

        public String uuid() {
            return "3e8354f7-31b3-4862-a52e-ff41a1ee60af";
        }
    };

    public static EncounterTypeDescriptor TRANSFER_OUT = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "Transfer Out";
        }

        @Override
        public String description() {
            return "Transfer out encounter";
        }

        public String uuid() {
            return "e305d98a-d6a2-45ba-ba2a-682b497ce27c";
        }
    };

    public static EncounterTypeDescriptor ART_REGIMEN_CHANGE = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "ART Regimen Change";
        }

        @Override
        public String description() {
            return "ART Regimen Switch or Change Encounter";
        }

        public String uuid() {
            return "c11774c1-3b4a-4bdb-a847-6060895e006d";
        }
    };

    public static EncounterTypeDescriptor COVID19_ENROLLMENT = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "Covid19 Case Investigation";
        }

        @Override
        public String description() {
            return "Covid19 Case Investigation Encounter";
        }

        public String uuid() {
            return "422ee220-9e83-451d-9b25-79a688a0413a";
        }
    };

    public static EncounterTypeDescriptor COVID19_FOLLOWUP = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "Covid19 Clinical Management";
        }

        @Override
        public String description() {
            return "Covid19 Clinical Management Encounter";
        }

        public String uuid() {
            return "cd9a2698-206f-44f3-a888-f824544413b4";
        }
    };
    public static EncounterTypeDescriptor COVID19_DISCHARGE = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "Covid19 Discharge";
        }

        @Override
        public String description() {
            return "Covid19 Discharge Encounter";
        }

        public String uuid() {
            return "482e4b17-fb9c-4937-a1cf-9052d3e3be68";
        }
    };
    public static EncounterTypeDescriptor COVID19_POSTMORTEM = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "Covid19 Postmortem";
        }

        @Override
        public String description() {
            return "Covid19 Postmortem Encounter";
        }

        public String uuid() {
            return "7f7f59dc-defe-11eb-ba80-0242ac130004";
        }
    };
    public static EncounterTypeDescriptor COVID19_REFERRAL = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "Covid19 Referral";
        }

        @Override
        public String description() {
            return "Covid19 Referral Encounter";
        }

        public String uuid() {
            return "afcdfcd8-defe-11eb-ba80-0242ac130004";
        }
    };
    public static EncounterTypeDescriptor EMERGENCY_ART_SERVICE = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "Emergency ART Service Encounter";
        }

        @Override
        public String description() {
            return "Emergency ART Service Encounter";
        }

        public String uuid() {
            return "d18bd8f2-dfcd-11eb-ba80-0242ac130004";
        }
    };

    public static EncounterTypeDescriptor COVID19_VACCINATION_TRACKING = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "Covid19 Vaccination Tracking Encounter";
        }

        @Override
        public String description() {
            return "Covid19 vaccination tracking for clients active on ART";
        }

        public String uuid() {
            return "c392cf0e-5024-4f47-9ed9-e10e307e942f";
        }
    };

    public static EncounterTypeDescriptor CACX_SCREENING_LOG = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "CaCx Screening Eligibility Log Encounter";
        }

        @Override
        public String description() {
            return "CaCx Screening Eligibility Log Encounter";
        }

        public String uuid() {
            return "91725548-2d90-4b28-be6d-0509ba37bb0f";
        }
    };

    public static EncounterTypeDescriptor CACX_TREATMENT_REGISTER = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "CaCx Treatment Encounter";
        }

        @Override
        public String description() {
            return "CaCx Treatment Encounter";
        }

        public String uuid() {
            return "6d647bd4-33d6-4d04-a04a-595d2159b456";
        }
    };

    public static EncounterTypeDescriptor SMS_ENROLLMENT = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "SMS Enrollment Encounter";
        }

        @Override
        public String description() {
            return "SMS Enrollment Encounter";
        }

        public String uuid() {
            return "05fa932f-4203-43c9-8985-60f2bea8a773";
        }
    };

    public static EncounterTypeDescriptor FAMILY_PLANNING_ENCOUNTER = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "Family Planning Encounter Type";
        }

        @Override
        public String description() {
            return "Family Planning Encounter Type";
        }

        public String uuid() {
            return "1668ef2e-1aca-4b5d-931d-696b272eea0f";
        }
    };

    public static EncounterTypeDescriptor NEW_BORN_INPATIENT_ENCOUNTER = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "New Born In Patient Register";
        }

        @Override
        public String description() {
            return "This is an encounter for new borns admitted. ";
        }

        public String uuid() {
            return "121ce4fe-1279-4443-b391-0f3fd5b2168d";
        }
    };

    public static EncounterTypeDescriptor CHILD_HEALTH_ENCOUNTER = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "Child Health Encounter";
        }

        @Override
        public String description() {
            return "This is an encounter assessing Children's Health";
        }

        public String uuid() {
            return "87a0d5b1-53dc-4269-bf39-ada0d5d16c0c";
        }
    };

    public static EncounterTypeDescriptor IN_PATIENT_ENCOUNTER = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "In Patient Encounter";
        }

        @Override
        public String description() {
            return "This is an encounter  for patients admitted into the hospital ";
        }

        public String uuid() {
            return "5ef205bc-136a-469b-b074-5f39488db91a";
        }
    };

    public static EncounterTypeDescriptor LAB_REQUEST_ENCOUNTER = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "Lab Request Encounter";
        }

        @Override
        public String description() {
            return "Lab Request Encounter";
        }

        public String uuid() {
            return "cbf01392-ca29-11e9-a32f-2a2ae2dbcce4";
        }
    };
    public static EncounterTypeDescriptor INTER_FACILITY_LINKAGE_ENCOUNTER = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "Facility Linkage Encounter";
        }

        @Override
        public String description() {
            return "Facility Linkage Encounter";
        }

        public String uuid() {
            return "786c576b-b70f-4235-93ea-fbce1a3f38c4";
        }
    };

    public static EncounterTypeDescriptor MOBILITY_SCREENING = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "Mobility Screening  Encounter";
        }

        @Override
        public String description() {
            return "Mobility Screening Encounter";
        }

        public String uuid() {
            return "b57e1835-4ee2-47fa-9569-c700c39c169a";
        }
    };

    public static EncounterTypeDescriptor FAMILY_TRACKING = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "ART Card - Family Tracking";
        }

        @Override
        public String description() {
            return "This encounter supports tracking family members of a patient";
        }

        public String uuid() {
            return "591cba6a-5c5f-11ee-8c99-0242ac120002";
        }
    };
    public static EncounterTypeDescriptor DEATH_NOTIFICATION = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "DEATH AND CERTIFICATION";
        }

        @Override
        public String description() {
            return "Notification of Death and Certification of Cause of Death";
        }

        public String uuid() {
            return "e75c856a-9e91-4ffb-bf43-1b0450b4ff8c";
        }
    };

    public static EncounterTypeDescriptor REGISTRATION = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "Registration";
        }

        @Override
        public String description() {
            return "Encounter type for extra fields on registration";
        }

        public String uuid() {
            return "1458b726-4a62-4444-be97-bb3e08c73745";
        }
    };


    public static EncounterTypeDescriptor MEDICATION_ORDER = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "Medication Order";
        }

        @Override
        public String description() {
            return "The encounter for ordering drugs for patient";
        }

        public String uuid() {
            return "dbe038cd-cad5-439d-a761-a6d6d680219c";
        }
    };

    public static EncounterTypeDescriptor TB_SCREENING_ENCOUNTER = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "TB Screening";
        }

        @Override
        public String description() {
            return "Encounter type for screening TB patient";
        }

        public String uuid() {
            return "08478ad9-ccc1-4cbe-9e55-473447984158";
        }
    };

    public static EncounterTypeDescriptor MONKEY_POX_SCREENING_ENCOUNTER = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "Monkey Pox Screening";
        }

        @Override
        public String description() {
            return "Encounter type for screening MPOX patient";
        }

        public String uuid() {
            return "09478ad9-ccc1-4cbe-9e55-473447984158";
        }
    };

    public static EncounterTypeDescriptor PROCEDURE_RESULTS_ENCOUNTER = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "Procedure Results";
        }

        @Override
        public String description() {
            return "Encounter type for processing procedure results";
        }

        public String uuid() {
            return "a4870f6d-ea06-4bbe-b775-bcbfb0816dbf";
        }
    };

    public static EncounterTypeDescriptor HIV_SELF_TESTING_ENCOUNTER = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "HIV Self Testing Encounter";
        }

        @Override
        public String description() {
            return "Encounter type for to record clients accessing HIV Self-Test kits at Health facility and community distribution points";
        }

        public String uuid() {
            return "b75fc5be-83a6-4771-afae-87d1b68af4f7";
        }
    };

    public static EncounterTypeDescriptor HTS_CONTACT_TRACING_ENCOUNTER = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "HTS Contact Tracing Encounter";
        }

        @Override
        public String description() {
            return "Encounter to record all clients eligible for index testing and SNS";
        }

        public String uuid() {
            return "3849c43c-f14d-40ab-80d7-670cf251e525";
        }
    };

    public static EncounterTypeDescriptor GENERAL_CLINICAL_ASSESSMENT_ENCOUNTER  = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "General Clinical Assessment";
        }

        @Override
        public String description() {
            return "Encounter used for general clinical assessment";
        }

        public String uuid() {
            return "7b2eed30-59cb-4a0e-9741-a568952b3d76";
        }
    };

    public static EncounterTypeDescriptor ATTACHMENT_UPLOAD = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "Attachment Upload";
        }

        @Override
        public String description() {
            return "Encounters used to record uploads of attachments.";
        }

        public String uuid() {
            return "5021b1a1-e7f6-44b4-ba02-da2f2bcf8718";
        }
    };

    public static EncounterTypeDescriptor DISCHARGE = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "Discharge";
        }

        @Override
        public String description() {
            return "Indicates that a patient's inpatient care at the hospital is ending, and they are expected to leave soon.";
        }

        public String uuid() {
            return "181820aa-88c9-479b-9077-af92f5364329";
        }
    };

    public static EncounterTypeDescriptor ADMISSION = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "Admission";
        }

        @Override
        public String description() {
            return "Indicates that the patient has been admitted for inpatient care, and is not expected to leave the hospital unless discharged.";
        }

        public String uuid() {
            return "e22e39fd-7db2-45e7-80f1-60fa0d5a4378";
        }
    };

    public static EncounterTypeDescriptor VISIT_NOTE = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "Visit Note";
        }

        @Override
        public String description() {
            return "Encounter where a full or abbreviated examination is done, usually leading to a presumptive or confirmed diagnosis, recorded by the examining clinician.";
        }

        public String uuid() {
            return "d7151f82-c1f3-4152-a605-2f9ea7414a79";
        }
    };

    public static EncounterTypeDescriptor CHECK_OUT = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "Check Out";
        }

        @Override
        public String description() {
            return "The patient is explicitly leaving the hospital/clinic. (Usually no formal encounter is captured for this.)";
        }

        public String uuid() {
            return "25a042b2-60bc-4940-a909-debd098b7d82";
        }
    };

    public static EncounterTypeDescriptor CHECK_IN = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "Check In";
        }

        @Override
        public String description() {
            return "Indicates the patient has done the required paperwork and check-in to begin a visit to the clinic/hospital.";
        }

        public String uuid() {
            return "ca3aed11-1aa4-42a1-b85c-8332fc8001fc";
        }
    };

    public static EncounterTypeDescriptor TRANSFER = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "Transfer";
        }

        @Override
        public String description() {
            return "Indicates that a patient is being transferred into a different department within the hospital. (Transfers out of the hospital should not use this encounter type.)";
        }

        public String uuid() {
            return "7b68d557-85ef-4fc8-b767-4fa4f5eb5c23";
        }
    };

    public static EncounterTypeDescriptor VITALS = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "Vitals";
        }

        @Override
        public String description() {
            return "For capturing vital signs";
        }

        public String uuid() {
            return "67a71486-1a54-468f-ac3e-7091a9a79584";
        }
    };

    public static EncounterTypeDescriptor LAB_ENCOUNTER = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "LAB Encounter";
        }

        @Override
        public String description() {
            return "Lab Encounter";
        }

        public String uuid() {
            return "214e27a1-606a-4b1e-a96e-d736c87069d5";
        }
    };

    public static EncounterTypeDescriptor SMC_ENCOUNTER = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "SMC - Encounter";
        }

        @Override
        public String description() {
            return "An encounter when a patient gets SMC services";
        }

        public String uuid() {
            return "244da86d-f80e-48fe-aba9-067f241905ee";
        }
    };

    public static EncounterTypeDescriptor EID_CARD_ENCOUNTER = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "EID Card - Encounter";
        }

        @Override
        public String description() {
            return "Outpatient Child Return Visit";
        }

        public String uuid() {
            return "4345dacb-909d-429c-99aa-045f2db77e2b";
        }
    };

    public static EncounterTypeDescriptor ART_HEALTH_EDUCATION = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "ART Card - Health Education Encounter";
        }

        @Override
        public String description() {
            return "An Health education encounter created when a patient gets health education";
        }

        public String uuid() {
            return "6d88e370-f2ba-476b-bf1b-d8eaf3b1b67e";
        }
    };

    public static EncounterTypeDescriptor EID_CARD_SUMMARY = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "EID Card - Summary";
        }

        @Override
        public String description() {
            return "Outpatient Children Initial Visit";
        }

        public String uuid() {
            return "9fcfcc91-ad60-4d84-9710-11cc25258719";
        }
    };

    public static EncounterTypeDescriptor MATERNITY_ENCOUNTER = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "Maternity - Encounter";
        }

        @Override
        public String description() {
            return "When a mother comes to a health facility to deliver";
        }

        public String uuid() {
            return "a9f11592-22e7-45fc-904d-dfe24cb1fc67";
        }
    };

    public static EncounterTypeDescriptor EARLY_INFANT_DIAGNOSIS_REQUEST = new EncounterTypeDescriptor() {
        @Override
        public String name() {
            return "Early Infant Diagnosis (EID) Request";
        }

        @Override
        public String description() {
            return "";
        }

        public String uuid() {
            return "d211f5a7-b0b7-4021-8224-72c58866600e";
        }
    };

}