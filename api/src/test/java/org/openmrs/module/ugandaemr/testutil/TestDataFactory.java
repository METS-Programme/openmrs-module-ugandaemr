package org.openmrs.module.ugandaemr.testutil;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.Program;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.ugandaemr.api.dto.StabilityCriteria;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Factory for creating test data objects for unit testing.
 * Provides convenient methods for creating complex OpenMRS domain objects with minimal boilerplate.
 */
public class TestDataFactory {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Creates a test patient with basic demographics.
     *
     * @param patientId the patient ID to use
     * @return a configured Patient object
     */
    public static Patient createTestPatient(int patientId) {
        Patient patient = new Patient();
        patient.setPatientId(patientId);
        patient.setGender("M");

        patient.addName(new org.openmrs.PersonName("John", null, "Doe"));
        patient.setBirthdate(new Date());

        return patient;
    }

    /**
     * Creates a test patient with identifiers.
     *
     * @param patientId the patient ID to use
     * @param identifierValue the identifier value
     * @return a configured Patient object with identifiers
     */
    public static Patient createPatientWithIdentifier(int patientId, String identifierValue) {
        Patient patient = createTestPatient(patientId);

        PatientIdentifierType identifierType = new PatientIdentifierType();
        identifierType.setName("Test Identifier");
        identifierType.setPatientIdentifierTypeId(1);

        PatientIdentifier identifier = new PatientIdentifier();
        identifier.setIdentifier(identifierValue);
        identifier.setIdentifierType(identifierType);
        identifier.setLocation(createTestLocation());

        Set<PatientIdentifier> identifiers = new HashSet<>();
        identifiers.add(identifier);
        patient.setIdentifiers(identifiers);

        return patient;
    }

    /**
     * Creates a test location.
     *
     * @return a configured Location object
     */
    public static Location createTestLocation() {
        Location location = new Location();
        location.setLocationId(1);
        location.setName("Test Location");
        location.setUuid("test-location-uuid");
        return location;
    }

    /**
     * Creates a test encounter.
     *
     * @param encounterId the encounter ID to use
     * @param patient the patient for the encounter
     * @return a configured Encounter object
     */
    public static Encounter createTestEncounter(int encounterId, Patient patient) {
        Encounter encounter = new Encounter();
        encounter.setEncounterId(encounterId);
        encounter.setEncounterDatetime(new Date());
        encounter.setPatient(patient);
        encounter.setLocation(createTestLocation());

        EncounterType encounterType = new EncounterType();
        encounter.setEncounterType(encounterType);

        return encounter;
    }

    /**
     * Creates a test observation.
     *
     * @param conceptId the concept ID for the observation
     * @param value the value for the observation
     * @return a configured Obs object
     */
    public static Obs createTestObservation(Integer conceptId, Object value) {
        Obs obs = new Obs();
        obs.setObsId(1);

        Concept concept = new Concept();
        concept.setConceptId(conceptId);
        obs.setConcept(concept);
        obs.setObsDatetime(new Date());

        if (value instanceof Integer) {
            obs.setValueNumeric(((Integer) value).doubleValue());
        } else if (value instanceof String) {
            obs.setValueText((String) value);
        } else if (value instanceof Date) {
            obs.setValueDatetime((Date) value);
        } else if (value instanceof Concept) {
            obs.setValueCoded((Concept) value);
        }

        return obs;
    }

    /**
     * Creates a test order.
     *
     * @param orderId the order ID to use
     * @param patient the patient for the order
     * @return a configured Order object
     */
    public static Order createTestOrder(int orderId, Patient patient) {
        Order order = new Order();
        order.setOrderId(orderId);
        order.setPatient(patient);
        order.setDateActivated(new Date());
        order.setUuid("test-order-uuid");
        return order;
    }

    /**
     * Creates a test concept.
     *
     * @param conceptId the concept ID to use
     * @param displayName the display name for the concept
     * @return a configured Concept object
     */
    public static Concept createTestConcept(int conceptId, String displayName) {
        Concept concept = new Concept();
        concept.setConceptId(conceptId);
        concept.setUuid("test-concept-uuid-" + conceptId);

        org.openmrs.ConceptName name = new org.openmrs.ConceptName();
        name.setName(displayName);
        concept.addName(name);

        return concept;
    }

    /**
     * Creates a test program.
     *
     * @param programId the program ID to use
     * @return a configured Program object
     */
    public static Program createTestProgram(int programId) {
        Program program = new Program();
        program.setProgramId(programId);
        program.setName("Test Program");
        program.setUuid("test-program-uuid");
        return program;
    }

    /**
     * Creates a test user.
     *
     * @param userId the user ID to use
     * @param username the username
     * @return a configured User object
     */
    public static User createTestUser(int userId, String username) {
        User user = new User();
        user.setUserId(userId);
        user.setUsername(username);
        return user;
    }

    /**
     * Creates a test StabilityCriteria object.
     *
     * @return a configured StabilityCriteria object
     */
    public static StabilityCriteria createTestStabilityCriteria() {
        StabilityCriteria criteria = new StabilityCriteria();
        criteria.setUuid("test-stability-uuid");
        criteria.setOnThirdRegimen(false);
        criteria.setConceptForClinicStage(1);
        criteria.setRegimenObsConceptId(100);
        criteria.setCurrentRegimenObsConceptId(200);
        criteria.setRegimenBeforeDTGObsValueConceptId(300);
        criteria.setSputumResultObsValueConceptId(400);
        criteria.setBaselineRegimenConceptId(500);
        criteria.setEnableCliniciansMakeStabilityDecisions("true");
        return criteria;
    }

    /**
     * Creates a date from a string.
     *
     * @param dateString the date string in format "yyyy-MM-dd"
     * @return a Date object
     */
    public static Date createDate(String dateString) {
        try {
            return DATE_FORMAT.parse(dateString);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse date: " + dateString, e);
        }
    }

    /**
     * Creates a set of test observations.
     *
     * @param conceptIdsAndValues array of conceptId, value pairs
     * @return a set of Obs objects
     */
    public static Set<Obs> createTestObservations(Object... conceptIdsAndValues) {
        Set<Obs> observations = new HashSet<>();

        for (int i = 0; i < conceptIdsAndValues.length; i += 2) {
            Integer conceptId = (Integer) conceptIdsAndValues[i];
            Object value = conceptIdsAndValues[i + 1];
            observations.add(createTestObservation(conceptId, value));
        }

        return observations;
    }
}