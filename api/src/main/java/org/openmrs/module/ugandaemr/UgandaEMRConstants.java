package org.openmrs.module.ugandaemr;

/**
 * Uganda customization specific constants
 * Created by ssmusoke on 06/01/2016.
 */
public class UgandaEMRConstants {
    /**
     * Module ID
     */
    public static final String MODULE_ID = "ugandaemr";
    public static final String UGANDAEMR_MODULE_ID = "ugandaemr";
    
    /**
     * Global property names
     */
    public static final String MODULE_PRIVILEGE = "UgandaEMR Privilege";
    public static final String GP_HEALTH_CENTER_NAME = MODULE_ID + ".healthCenterName";
    public static final String GP_HEALTH_CENTER_NAME_VALUE = "Health Center Name";
    public static final String GP_HEALTH_CENTER_NAME_DEFAULT_ALERT_MESSAGE="The Health Center Name is not set please go to admin then Settings then Ugandaemr and set it";

    public static final String GP_DHIS2= "ugandaemr.dhis2.organizationuuid";
    public static final String GP_DHIS2_VALUE = "eg d06ace3e-9c46-11e7-abc4-cec278b6b50a";
    public static final String GP_NHPI = MODULE_ID + ".nhpi";
    public static final String GP_NHPI_VALUE = "HFT2ZBPE8";
    public static final String GP_NHPI_DESCRIPTION = "Allows one to set the National Health Provider Identifier";
    public static final String GP_NHPI_DEFAULT_ALERT_MESSAGE="The National Health Provider Identifier is not set please go to admin then Settings then Ugandaemr and set it";
    public static final String GP_DHIS2_DEFAULT_ALERT_MESSAGE="The organization code in DHIS2 is not set please go to admin then Settings then Ugandaemr and set it";

    public static final String GP_DSDM_PROGRAM_UUID_NAME="ugandaemr.dsdm.programsuuid";

    public static final String GP_DSDM_CONCEPT_ID="ugandaemr.dsdm.conceptId";


    /**x
     * Concepts
     */
    public static final String UNIT_TB_NUMBER = "304df0d0-afe4-4a61-a917-d684b100a65a";

    public static final String HSD_TB_NUMBER = "d1cda288-4853-4450-afbc-76bd4e65ea70";

    public static final String DISTRICT_TB_NUMBER = "67e9ec2f-4c72-408b-8122-3706909d77ec";

    public static final  String TRANSFER_OUT_DATE_CONCEPT_ID="99165";
    public static final  String TRANSFER_OUT_PLACE_CONCEPT_ID="90211";
    public static final  String TRANSFER_FROM_CLINIC_CONCEPT_ID="164993";
    public static final  String ART_CLINIC_CONCEPT_ID="165047";

    public static final  String TRANSFER_IN_CONCEPT_ID="99110";
    public static final  String TRANSFER_IN_FROM_CONCEPT_ID="165047";
    public static final  String TRANSFER_IN_FROM_PLACE_CONCEPT_ID="99109";


    public static final String  FACILITY_VISIT_TYPE_UUID="7b0f5697-27e3-40c4-8bae-f4049abfb4ed";
    public  static final String ART_TRANSFER_SCHEDULE_NAME="ART Summary Patient Transfer Migration";
    public static final String CHANGE_MESSAGE_FOR_TRANSFERS = "New Observation on patient transfers";

    public static final String PATIENT_TRANSERRED_OUT ="transferredOut";
    public  static final String PATIENT_TRANSFERED_OUT_LOCATION ="transferredOutTo";
    public static final String PATIENT_TRANSFERED_OUT_DATE = "dateTransferredOut";

    public static final String PATIENT_TRANSERRED_IN ="transferredIn";
    public  static final String PATIENT_TRANSFERED_IN_LOCATION ="transferredInTo";
    public static final String PATIENT_TRANSFERED_IN_DATE = "dateTransferredIn";


    public static final String TRIAGE_LOCATION_UUID = "ff01eaab-561e-40c6-bf24-539206b521ce";
    public static final String PHARMACY_LOCATION_UUID = "3ec8ff90-3ec1-408e-bf8c-22e4553d6e17";
    public static final String LAB_LOCATION_UUID = "ba158c33-dc43-4306-9a4a-b4075751d36c";
    public static final String RADIOLOGY_LOCATION_UUID = "f586757c-3846-11ee-be56-0242ac120002";

    public static final String DRUG_SET_CLASS = "Drug";
    public static final String LAB_SET_CLASS = "LabSet";
    public static final String RADIOLOGY_SET_CLASS = "Radiology/Imaging Procedure";
    public static final String TEST_SET_CLASS = "Test";

    public static final String ORDER_TYPE_DRUG_UUID = "131168f4-15f5-102d-96e4-000c29c2a5d7";
    public static final String ORDER_TYPE_LAB_UUID =  "52a447d3-a64a-11e3-9aeb-50e549534c5e";

    public static final int MEDICATION_ORDER_CONCEPT_ID = 1282;
    public static final int MEDICATION_STRENGTH_CONCEPT_ID = 1444;
    public static final int MEDICATION_QUANTITY_CONCEPT_ID = 160856;
    public static final int ARV_MEDICATION_QUANTITY_CONCEPT_ID = 99038;
    public static final int MEDICATION_QUANTITY_UNIT_CONCEPT_ID = 165791;
    public static final int MEDICATION_FORM_CONCEPT_ID = 1519;
    public static final int MEDICATION_START_DATE_CONCEPT_ID = 1190;
    public static final int MEDICATION_END_DATE_CONCEPT_ID = 1191;
    public static final int MEDICATION_INDICATOR_CONCEPT_ID = 160742;
    public static final int MEDICATION_DURATION_CONCEPT_ID = 159368;
    public static final int ARV_MEDICATION_DURATION_CONCEPT_ID = 99036;
    public static final int MEDICATION_DURATION_UNIT_CONCEPT_ID = 1732;
    public static final int MEDICATION_FREQUENCY = 160855;
    public static final int MEDICATION_DOSE_INSTRUCTION = 175387;
    public static final int CURRENTLY_TAKING_MEDICATION_CONCEPT_ID = 159367;
    public static final int MEDICATION_COMMENT_CONCEPT_ID = 160632;

    public static final int DISPENSING_LOCATION_CONCEPT_ID = 166968;

    public static final int DRUG_ID_CONCEPT_ID = 166970;
    public static final int DEFALUT_ROUTE_CONCEPT_ID = 160240;
    public static final int DEFALUT_DOSE_UNIT_CONCEPT_ID = 162366;
    public static final int DEFALUT_DISPENSING_UNIT_CONCEPT_ID = 162399;
    public static final int DEFALUT_DURATION_UNIT_CONCEPT_ID = 1072;

    public static final int MEDICATION_DISPENSE_SET = 163711;
    public static final int MEDICATION_DISPENSE_QUANTITY = 1443;
    public static final int MEDICATION_DISPENSE_RECEIVED_AT_VIST = 1276;
    public static final int MEDICATION_DISPENSE_RECEIVED_AT_VIST_YES = 1065;
    public static final int MEDICATION_DISPENSE_RECEIVED_AT_VIST_NO = 1066;

    public static final int CONCEPT_ID_NEXT_APPOINTMENT = 5096;
    public static final int CONCEPT_ID_TRANSFERED_OUT = 90306;

    public static final String  DEFALUT_ORDER_FREQUECNY_UUID = "160862OFAAAAAAAAAAAAAAA";

    public static final String  DAY_START_TIME = "00:00:00";
    public static final String  DAY_END_TIME = "23:59:59";



    public static final String QUEUE_STATUS_PENDING = "pending";
    public static final String QUEUE_STATUS_COMPLETED = "completed";
    public static final String QUEUE_STATUS_ACTIVE = "active";
    public static final String QUEUE_STATUS_SENT_TO_LAB = "sent to lab";
    public static final String QUEUE_STATUS_FROM_LAB = "from lab";
    public static final String QUEUE_STATUS_SENT_TO_PHARMACY = "sent to pharmacy";
    public static final String QUEUE_STATUS_HAS_RESULTS = "has results";


    public static final String PROCESSED_ORDER_WITH_RESULT_QUERY = "select orders.order_id from orders  inner join test_order on (test_order.order_id=orders.order_id) inner join obs on (orders.order_id=obs.order_id) where orders.accession_number!=\"\" and specimen_source!=\"\" AND orders.date_created BETWEEN \"%s\" AND \"%s\"";

    public static final String SPECIMEN_MANUAL_UPDATE_QUERY="UPDATE test_order set specimen_source=%s where order_id=%s";
    public static final String PROCESSED_ORDER_WITH_RESULT_OF_ENCOUNTER_QUERY = "select orders.order_id from orders  inner join test_order on (test_order.order_id=orders.order_id) inner join obs on (orders.order_id=obs.order_id) where orders.accession_number!=\"\" and fulfiller_status=\"COMPLETED\" AND orders.encounter_id=%s";

    public static final String PROCESSED_ORDER_WITH_RESULT_FOR_PATIENT_QUERY = "select orders.order_id from orders  inner join test_order on (test_order.order_id=orders.order_id) inner join obs on (orders.order_id=obs.order_id) where orders.fulfiller_status=\"COMPLETED\" AND orders.patient_id=%s";

    public static final String PROCESSED_ORDER_WITHOUT_RESULT_QUERY = "select orders.order_id from orders  inner join test_order on (test_order.order_id=orders.order_id) where accession_number!=\"\" and specimen_source!=\"\" AND orders.date_created BETWEEN \"%s\" AND \"%s\"";

    public static final String PROCESSED_ORDER_WITHOUT_NO_DATE_FILTER_RESULT_QUERY = "select orders.order_id from orders  inner join test_order on (test_order.order_id=orders.order_id) where accession_number!=\"\" and specimen_source!=\"\"";

    public static final String ENCOUNTER_ROLE = "240b26f9-dd88-4172-823d-4a8bfeb7841f";
    public static final String ENCOUNTER_ROLE_PHARMACIST= "8da340f3-c690-439f-b21f-3a8367ff4057";

    public  static final String ENCOUNTER_TYPE_DISPENSE_UUID="22902411-19c1-4a02-b19a-bf1a9c24fd51";
    public  static final String DISPENSE_FORM_UUID="340fe8d8-4984-11ea-b77f-2e728ce88125";

    public static final String CARE_SETTING_OPD = "Outpatient";


    public static final int TB_HEALTH_UNIT_NUMBER_CONCEPT_ID = 165826;
    public static final String TB_HEALTH_UNIT_NUMBER_PROGRAM_ATTRIBUTE_TYPE_UUID = "af9baf06-d245-11ea-87d0-0242ac130003";
    public static final int TB_DISTRICT_NUMBER_CONCEPT_ID = 99031;
    public static final String TB_DISTRICT_NUMBER_PROGRAM_ATTRIBUTE_TYPE_UUID = "af9bac5e-d245-11ea-87d0-0242ac130003";
    public static final int TB_REGIMEN_CONCEPT_ID = 159958;
    public static final String TB_REGIMEN_PROGRAM_ATTRIBUTE_TYPE_UUID = "af9bb0c8-d245-11ea-87d0-0242ac130003";
    public static final int DR_TB_NUMBER_PROGRAM_CONCEPT_ID = 165843;
    public static final String DR_TB_NUMBER_PROGRAM_ATTRIBUTE_TYPE_UUID = "1628fa4e-fda2-11ea-adc1-0242ac120002";
    public static final String CONCEPT_REASON_FOR_NEXT_APPOINTMENT = "160288AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";



    public static final String GP_DEFAULT_LOCATION = MODULE_ID + ".defaultLocation";

    public static final String HIV_ELIGIBILITY_COHORT_UUID= "56b082f8-f956-499d-a8c2-d9b32a067e65";

    public static final String TB_ELIGIBILITY_COHORT_UUID= "0aa9ba5f-d44a-4b31-aff1-3a046bd8e5e0";

    public static final String HIV_ELIGIBILITY_QUERY = "select DISTINCT person_id from obs  where concept_id=175333 and value_coded=703 and\n" +
            "            person_id not in (select cm.patient_id from cohort_member cm inner join cohort ch on(cm.cohort_id = ch.cohort_id)\n" +
            "           where  ch.uuid='56b082f8-f956-499d-a8c2-d9b32a067e65') and person_id not in\n" +
            "           (select patient_id from patient_program inner join program on(patient_program.program_id = program.program_id));";

    public static final String TB_ELIGIBILITY_QUERY = "select person_id from obs  where (concept_id=162202 OR concept_id=165291 OR concept_id=165414) and value_coded=703\n" +
            "        and person_id not in (select cm.patient_id from cohort_member cm inner join cohort ch on(cm.cohort_id = ch.cohort_id)\n" +
            "            where  ch.uuid='0aa9ba5f-d44a-4b31-aff1-3a046bd8e5e0') and person_id not in\n" +
            "    (select patient_id from patient_program inner join program on(patient_program.program_id = program.program_id));";

    public static final String HIV_PROGRAM = "HIV Program";
    public static final String HIV_PROGRAM_UUID = "18c6d4aa-0a36-11e7-8dbb-507b9dc4c741";
    public static final String HIV_DISCONTINUATION_FORM_NAME = "HIV Discontinuation";
    public static final String HIV_DISCONTINUATION_FORM_UUID ="1c7baac0-87cf-4d12-a938-81ea0d6f6448";
    public static final String HIV_ENROLLMENT_FORM_NAME ="HMIS ACP HIV 003: HIV CARE/ART CARD - Summary Form";
    public static final String HIV_ENROLLMENT_FORM_UUID ="52653a60-8300-4c13-be4d-4b746da06fee";

    public static final String TB_PROGRAM = "TB Program";
    public static final String TB_PROGRAM_UUID="9dc21a72-0971-11e7-8037-507b9dc4c741";
    public static final String TB_DISCONTINUATION_FORM_NAME = "TB Discontinuation";
    public static final String TB_DISCONTINUATION_FORM_UUID ="7d0dd4eb-3ff7-412c-97e4-808ac8251a83";
    public static final String TB_ENROLLMENT_FORM_NAME ="DS TB: HMIS TB 003 - TB Client Card Enrollment Page";
    public static final String DR_TB_ENROLLMENT_FORM_UUID ="4929f668-f8d0-11ea-adc1-0242ac120002";
    public static final String DS_TB_ENROLLMENT_FORM_UUID ="b00452b6-bb7c-11ea-b3de-0242ac130004";
}
