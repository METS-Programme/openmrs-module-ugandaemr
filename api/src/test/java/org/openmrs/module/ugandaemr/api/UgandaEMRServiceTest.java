package org.openmrs.module.ugandaemr.api;

import org.junit.Test;

/**
 * Test for generating the UIC task for patients without the UIC
 */
import java.util.Date;
import java.util.List;


import org.junit.Before;
import org.mockito.Mock;

import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.Concept;
import org.openmrs.api.PatientService;
import org.openmrs.api.OrderService;
import org.openmrs.api.VisitService;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class UgandaEMRServiceTest extends BaseModuleContextSensitiveTest {

    protected static final String UGANDAEMR_STANDARD_DATASET_XML = "org/openmrs/module/ugandaemr/include/standardTestDataset.xml";
    protected static final String TEST_ORDER_STANDARD_DATASET_XML = "org/openmrs/module/ugandaemr/include/TestOrderDataset.xml";


    @Mock
    private OrderService orderService;

    @Mock
    private ConceptService conceptService;

    @Mock
    private Order order;

    @Mock
    private Concept specimenConcept;
    private UgandaEMRService ugandaemrService;
    private PatientService patientService;
    private VisitService visitService;
    private AdministrationService administrationService;

    @Before
    public void initialize() throws Exception {
        executeDataSet(UGANDAEMR_STANDARD_DATASET_XML);
        executeDataSet(TEST_ORDER_STANDARD_DATASET_XML);
    }

    @Before
    public void setup() throws Exception {
        executeDataSet(UGANDAEMR_STANDARD_DATASET_XML);
        executeDataSet(TEST_ORDER_STANDARD_DATASET_XML);
        ugandaemrService = Context.getService(UgandaEMRService.class);
        patientService = Context.getPatientService();
        visitService = Context.getVisitService();
        administrationService = Context.getAdministrationService();
        conceptService=Context.getConceptService();
        orderService=Context.getOrderService();
    }

    @Test
    public void generateAndSaveUICForPatientsWithOut_shouldGenerateUICForPatientWithoutUIC() {

        List listBeforeGeneration = administrationService.executeSQL("select * from patient inner join patient_identifier pi on (patient.patient_id = pi.patient_id)  inner join patient_identifier_type pit on (pi.identifier_type = pit.patient_identifier_type_id) where pit.uuid='877169c4-92c6-4cc9-bf45-1ab95faea242'", true);

        assertEquals(0, listBeforeGeneration.size());

        ugandaemrService.generateAndSaveUICForPatientsWithOut();

        List listAfterGeneration = administrationService.executeSQL("select * from patient inner join patient_identifier pi on (patient.patient_id = pi.patient_id)  inner join patient_identifier_type pit on (pi.identifier_type = pit.patient_identifier_type_id) where pit.uuid='877169c4-92c6-4cc9-bf45-1ab95faea242'", true);

        assertNotEquals(0, listAfterGeneration.size());
    }

    @Test
    public void generatePatientUIC_shouldGenerateUIC() {
        Patient patient = patientService.getPatient(10003);

        String uniqueIdentifierCode = null;
        uniqueIdentifierCode = ugandaemrService.generatePatientUIC(patient);

        assertEquals("XX-0117-1-01140411011213", uniqueIdentifierCode);

    }

    @Test
    public void stopActiveOutPatientVisits_shouldCompleteAllVisitOfSetTypeInGlobalProperty() {

        assertTrue(visitService.getActiveVisitsByPatient(patientService.getPatient(10110)).size() > 0);

        ugandaemrService.stopActiveOutPatientVisits();

        assertTrue(visitService.getActiveVisitsByPatient(patientService.getPatient(10110)).size() == 0);

    }

    @Test
    public void isTransferredIn_ShouldReturnFalseWhenPatientIsNotTransferIn() {
        Patient patient = patientService.getPatient(10008);
        assertFalse(ugandaemrService.isTransferredIn(patient, new Date()));

    }

    @Test
    public void isTransferredOut_ShouldReturnFalseWhenPatientIsNotTransferredOut() {
        Context.getPatientService();
        Patient patient = patientService.getPatient(10008);
        assertFalse(ugandaemrService.isTransferredIn(patient, new Date()));

    }
/*
    @Test
    public void shouldCreateNewTestOrderWhenInstructionsProvidedAndNoAccessionNumberExists() {
        TestOrder originalOrder = (TestOrder) orderService.getOrderByUuid("aa946740-bbd1-413a-bd36-4a396716cbcf");

        TestOrder result = ugandaemrService.accessionLabTest(
                "aa946740-bbd1-413a-bd36-4a396716cbcf",
                "ACCN-001",
                "1002AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                "urgent"
        );

        assertNotNull(result);
        assertEquals("ACCN-001", result.getAccessionNumber());
        assertEquals("REFER TO URGENT", result.getInstructions());
        assertEquals(Order.Action.REVISE, result.getAction());
        assertEquals(Order.Urgency.STAT, result.getUrgency());
        assertEquals(Order.FulfillerStatus.IN_PROGRESS, result.getFulfillerStatus());
        assertEquals(originalOrder, result.getPreviousOrder());
    }

    @Test
    public void shouldUpdateFulfillerStatusWhenAccessionNumberAlreadyExistsAndIsDifferent() {
        TestOrder result = ugandaemrService.accessionLabTest(
                "aa946740-bbd1-413a-bd36-4a396716cbcf",
                "NEW-ACCN",
                "1002AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                "" // no instructions
        );

        assertNotNull(result);
        assertEquals("NEW-ACCN", result.getAccessionNumber());
        assertEquals(Order.FulfillerStatus.IN_PROGRESS, result.getFulfillerStatus());
    }

    @Test
    public void shouldUpdateFulfillerStatusAndSpecimenWhenNoNewOrderIsCreated() {
        TestOrder result = ugandaemrService.accessionLabTest(
                "aa946740-bbd1-413a-bd36-4a396716cbcf",
                "ACCN-001", // same as current
                "1002AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                "" // no instructions
        );

        assertNotNull(result);
        assertEquals("ACCN-001", result.getAccessionNumber());
        assertEquals(Order.FulfillerStatus.IN_PROGRESS, result.getFulfillerStatus());
    }*/



}