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

import org.openmrs.Location;
import org.openmrs.Provider;
import org.openmrs.module.ugandaemr.api.model.NonPatientQueue;
import org.openmrs.module.ugandaemr.api.model.NonPatientQueue.NonPatientQueueStatus;
import org.openmrs.module.ugandaemr.api.model.NonPatientQueue.NonPatientQueueType;

import static org.junit.jupiter.api.Assertions.*;

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


   /* @Test
    public void createQueueEntry_shouldCreateNonPatientQueueEntry() {
        Location currentLocation = Context.getLocationService().getLocation(1);
        Location locationTo = Context.getLocationService().getLocation(1);
        Location queueRoom = Context.getLocationService().getLocation(1);

        NonPatientQueue queue = ugandaemrService.createQueueEntry(
                "Visitor John",
                "0771234567",
                NonPatientQueueType.VISITOR,
                currentLocation,
                locationTo,
                queueRoom,
                1,
                "Visitor assistance"
        );

        assertNotNull(queue);
        assertNotNull(queue.getUuid());
        assertNotNull(queue.getTicketNumber());
        assertEquals("Visitor John", queue.getDisplayName());
        assertEquals("0771234567", queue.getPhoneNumber());
        assertEquals(NonPatientQueueType.VISITOR, queue.getQueueType());
        assertEquals(NonPatientQueueStatus.WAITING, queue.getStatus());
        assertEquals(currentLocation, queue.getCurrentLocation());
        assertEquals(locationTo, queue.getLocationTo());
        assertEquals(queueRoom, queue.getQueueRoom());
        assertEquals(Integer.valueOf(1), queue.getPriority());
        assertEquals("Visitor assistance", queue.getComment());
    }

    @Test
    public void saveQueueEntry_shouldPersistChanges() {
        Location currentLocation = Context.getLocationService().getLocation(1);
        Location locationTo = Context.getLocationService().getLocation(1);
        Location queueRoom = Context.getLocationService().getLocation(1);

        NonPatientQueue queue = ugandaemrService.createQueueEntry(
                "Admin Client",
                "0700000000",
                NonPatientQueueType.ADMIN,
                currentLocation,
                locationTo,
                queueRoom,
                2,
                "Admin support"
        );

        queue.setDisplayName("Updated Admin Client");
        queue.setComment("Updated comment");

        NonPatientQueue saved = ugandaemrService.saveQueueEntry(queue);

        assertNotNull(saved);
        assertEquals("Updated Admin Client", saved.getDisplayName());
        assertEquals("Updated comment", saved.getComment());
    }

    @Test
    public void getQueueEntryByUuid_shouldReturnSavedQueueEntry() {
        Location location = Context.getLocationService().getLocation(1);

        NonPatientQueue created = ugandaemrService.createQueueEntry(
                "Registration Visitor",
                null,
                NonPatientQueueType.REGISTRATION,
                location,
                location,
                location,
                1,
                null
        );

        NonPatientQueue fetched = ugandaemrService.getQueueEntryByUuid(created.getUuid());

        assertNotNull(fetched);
        assertEquals(created.getUuid(), fetched.getUuid());
        assertEquals(created.getTicketNumber(), fetched.getTicketNumber());
    }

    @Test
    public void getQueueEntryByTicketNumber_shouldReturnSavedQueueEntry() {
        Location location = Context.getLocationService().getLocation(1);

        NonPatientQueue created = ugandaemrService.createQueueEntry(
                "Records Visitor",
                null,
                NonPatientQueueType.RECORDS,
                location,
                location,
                location,
                1,
                null
        );

        NonPatientQueue fetched = ugandaemrService.getQueueEntryByTicketNumber(created.getTicketNumber());

        assertNotNull(fetched);
        assertEquals(created.getTicketNumber(), fetched.getTicketNumber());
        assertEquals(created.getUuid(), fetched.getUuid());
    }

    @Test
    public void callQueueEntry_shouldSetStatusCalledAndCalledAt() {
        Location location = Context.getLocationService().getLocation(1);
        Provider provider = Context.getProviderService().getAllProviders(false).get(0);

        NonPatientQueue created = ugandaemrService.createQueueEntry(
                "Visitor Call Test",
                null,
                NonPatientQueueType.VISITOR,
                location,
                location,
                location,
                1,
                null
        );

        NonPatientQueue called = ugandaemrService.callQueueEntry(created, provider);

        assertEquals(NonPatientQueueStatus.CALLED, called.getStatus());
        assertEquals(provider, called.getCalledBy());
        assertNotNull(called.getCalledAt());
    }

    @Test
    public void markArrived_shouldSetStatusArrivedAndArrivedAt() {
        Location location = Context.getLocationService().getLocation(1);

        NonPatientQueue created = ugandaemrService.createQueueEntry(
                "Visitor Arrived Test",
                null,
                NonPatientQueueType.VISITOR,
                location,
                location,
                location,
                1,
                null
        );

        NonPatientQueue arrived = ugandaemrService.markArrived(created);

        assertEquals(NonPatientQueueStatus.ARRIVED, arrived.getStatus());
        assertNotNull(arrived.getArrivedAt());
    }

    @Test
    public void startServing_shouldSetStatusServingAndStartedAt() {
        Location location = Context.getLocationService().getLocation(1);
        Provider provider = Context.getProviderService().getAllProviders(false).get(0);

        NonPatientQueue created = ugandaemrService.createQueueEntry(
                "Visitor Serving Test",
                null,
                NonPatientQueueType.VISITOR,
                location,
                location,
                location,
                1,
                null
        );

        NonPatientQueue serving = ugandaemrService.startServing(created, provider);

        assertEquals(NonPatientQueueStatus.SERVING, serving.getStatus());
        assertEquals(provider, serving.getServedBy());
        assertNotNull(serving.getStartedAt());
    }

    @Test
    public void completeQueueEntry_shouldSetStatusCompletedAndEndedAt() {
        Location location = Context.getLocationService().getLocation(1);
        Provider provider = Context.getProviderService().getAllProviders(false).get(0);

        NonPatientQueue created = ugandaemrService.createQueueEntry(
                "Visitor Complete Test",
                null,
                NonPatientQueueType.VISITOR,
                location,
                location,
                location,
                1,
                null
        );

        NonPatientQueue completed = ugandaemrService.completeQueueEntry(created, provider);

        assertEquals(NonPatientQueueStatus.COMPLETED, completed.getStatus());
        assertEquals(provider, completed.getServedBy());
        assertNotNull(completed.getEndedAt());
    }

    @Test
    public void getQueueEntriesByQueueRoomAndStatus_shouldReturnMatchingEntriesOnly() {
        Location room1 = Context.getLocationService().getLocation(1);
        Location room2 = Context.getLocationService().getLocation(2);
        Provider provider = Context.getProviderService().getAllProviders(false).get(0);

        NonPatientQueue waitingInRoom1 = ugandaemrService.createQueueEntry(
                "Waiting Room 1",
                null,
                NonPatientQueueType.VISITOR,
                room1,
                room1,
                room1,
                1,
                null
        );

        NonPatientQueue calledInRoom1 = ugandaemrService.createQueueEntry(
                "Called Room 1",
                null,
                NonPatientQueueType.VISITOR,
                room1,
                room1,
                room1,
                1,
                null
        );
        ugandaemrService.callQueueEntry(calledInRoom1, provider);

        ugandaemrService.createQueueEntry(
                "Waiting Room 2",
                null,
                NonPatientQueueType.VISITOR,
                room2,
                room2,
                room2,
                1,
                null
        );

        List<NonPatientQueue> waitingRoom1 = ugandaemrService.getQueueEntriesByQueueRoomAndStatus(
                room1, NonPatientQueueStatus.WAITING
        );

        assertNotNull(waitingRoom1);
        assertTrue(waitingRoom1.size() >= 1);
        assertTrue(waitingRoom1.stream().anyMatch(q -> q.getUuid().equals(waitingInRoom1.getUuid())));
        assertFalse(waitingRoom1.stream().anyMatch(q -> q.getUuid().equals(calledInRoom1.getUuid())));
    }

    @Test
    public void voidQueueEntry_shouldVoidQueueEntry() {
        Location location = Context.getLocationService().getLocation(1);

        NonPatientQueue created = ugandaemrService.createQueueEntry(
                "Void Test",
                null,
                NonPatientQueueType.OTHER,
                location,
                location,
                location,
                1,
                null
        );

        ugandaemrService.voidQueueEntry(created, "No longer needed");

        NonPatientQueue fetched = ugandaemrService.getQueueEntryByUuid(created.getUuid());

        assertNotNull(fetched);
        assertTrue(fetched.getVoided());
        assertEquals("No longer needed", fetched.getVoidReason());
    }
*/
}