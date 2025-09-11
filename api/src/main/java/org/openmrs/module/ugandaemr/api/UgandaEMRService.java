/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.ugandaemr.api;

import org.openmrs.*;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.ugandaemr.PublicHoliday;
import org.openmrs.module.ugandaemr.activator.Initializer;
import org.openmrs.module.ugandaemr.api.lab.OrderObs;
import org.openmrs.module.ugandaemr.api.queuemapper.CheckInPatient;
import org.openmrs.module.ugandaemr.api.queuemapper.PatientQueueVisitMapper;
import org.springframework.transaction.annotation.Transactional;
import org.openmrs.module.patientqueueing.mapper.PatientQueueMapper;
import org.openmrs.module.patientqueueing.model.PatientQueue;
import org.openmrs.module.ugandaemr.api.lab.mapper.OrderMapper;
import org.openmrs.module.ugandaemr.api.lab.util.TestResultModel;
import org.openmrs.module.ugandaemr.pharmacy.mapper.PharmacyMapper;


import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This service exposes module's core functionality. It is a Spring managed bean which is configured in moduleApplicationContext.xml.
 * <p>
 * It can be accessed only via Context:<br>
 * <code>
 * Context.getService(ugandaemrService.class).someMethod();
 * </code>
 *
 * @see org.openmrs.api.context.Context
 */
@Transactional
public interface UgandaEMRService extends OpenmrsService {

    /*
     * Link the infant with the A
     *
     */
    public void linkExposedInfantToMotherViaARTNumber(Patient infant, String motherARTNumber);

    public void linkExposedInfantToMotherViaARTNumber(Person infant, String motherARTNumber);

    public void setAlertForAllUsers(String alertMessage);

    /*
     * This method generates Unique identification Code
     * for all patients that do not have that id
     * It is generated based on the person demographics
     * submitted during patient registration
     * This has been designed to run as an automatic task
     * that run once a day for any patient that may not have the UIC already existing */

    /**
     * Generates a patients UIC (Unique Identifier Code) out of patient demographics
     * @param patient the patient to be generated a UIC for
     * @return String the UIC that has been generated
     */
    public String generatePatientUIC(Patient patient);

    /**
     * This method when called generates and saves UIC (Unique Identifier Code) for all patients who dont have the UIC
     */
    public void generateAndSaveUICForPatientsWithOut();

    /**
     * This Method stops all active out patient visits
     */
    public void stopActiveOutPatientVisits();


    /**
     * Gets transfer out encounters map.
     * @param patient the patient whose transfer out encounters are being queried
     * @param date the date of the transfer out it can be null
     * @return map of transfer out encounters for a patient.
     */
    public Map transferredOut(Patient patient, Date date);

    /**
     * Gets transfer in encounters.
     * @param patient the patient whose transfer in encounters are being queried
     * @param date the date of the transfer in it can be null
     * @return map of transfer in encounters for a patient.
     */
    public Map transferredIn(Patient patient, Date date);

    /**
     * Check if Patient is transferred out. This method depends on transferredOut(Patient patient) method
     * @param patient
     * @return boolean
     */
    public boolean isTransferredOut(Patient patient, Date date);


    /**
     * Check if Patient is a transfer in. This method depends on transferredIn(Patient patient) method
     * @param patient
     * @return boolean
     */
    public boolean isTransferredIn(Patient patient, Date date);

    /**
     * Transfer Information for patient
     * @param patient
     * @return Map
     */
    public List<Encounter> getTransferHistory(Patient patient);

    public List<PublicHoliday> getAllPublicHolidays() throws APIException;

    public PublicHoliday getPublicHolidayByDate(Date publicHolidayDate) throws APIException;

    public List<PublicHoliday> getPublicHolidaysByDate(Date publicHolidayDate) throws APIException;

    public PublicHoliday savePublicHoliday(PublicHoliday publicHoliday);

    public PublicHoliday getPublicHolidaybyUuid(String uuid);



    /**
     * Checks id a patient has an HIV Summary page
     *
     * @param patient           the patient to be changed
     * @param encounterTypeUUID the uuid for the HIV encounter Type
     * @return boolean
     */
    public Encounter hasHIVSummaryPage(Patient patient, String encounterTypeUUID);

    /**
     * Generates observation from an existing Observation
     * @param observations a list of observation to look into for a specific concept
     * @param lookUpConceptId the concept which will be used to lookup for an observation to be used to create another obs
     * @param conceptIDForNewObs the concept id which will be the concept for the new observation.
     * @param encounter the target encounter where the observation will be saved.
     * @return an observation with a encounter, value and a concept.
     */
    public Obs generateObsFromObs(Set<Obs> observations, Integer lookUpConceptId, Integer conceptIDForNewObs, Encounter encounter);

    /**
     * Helper Method to create Obs
     * @param concept   the concept
     * @param encounter the encounter where the obs will be created
     * @return a created obs
     */
    public Obs createNewObs(Concept concept, Encounter encounter);


    /**
     * @param patientQueueList
     * @return
     */
    public List<PatientQueueVisitMapper> mapPatientQueueToMapper(List<PatientQueue> patientQueueList);

    /**
     * Render Tests
     * @param test
     * @return
     */
    public Set<TestResultModel> renderTests(Order test);

    /**
     * Check if Sample ID exists
     * @param sampleId
     * @param orderNumber
     * @return
     * @throws ParseException
     */
    public boolean isSampleIdExisting(String sampleId, String orderNumber) throws ParseException;

    /**
     * Process Orders
     * @param query
     * @param asOfDate
     * @param includeProccesed
     * @return
     * @throws ParseException
     * @throws IOException
     */
    public SimpleObject getProcessedOrders(String query, Date asOfDate, boolean includeProccesed) throws ParseException,
            IOException;

    /**
     * Convert Orders to OrderMappers
     *
     * @param orders
     * @param fiterOutProccessed
     * @return
     */
    public Set<OrderMapper> processOrders(Set<Order> orders, boolean fiterOutProccessed);

    /**
     * @param encounter
     * @param testConcept
     * @param testGroupConcept
     * @param result
     * @param test
     */
    public void addLaboratoryTestObservation(Encounter encounter, Concept testConcept, Concept testGroupConcept,
                                             String result, Order test);

    /**
     * With Orders
     * @param patientQueueList
     * @return
     */
    public List<PatientQueueMapper> mapPatientQueueToMapperWithOrders(List<PatientQueue> patientQueueList);


    /**
     * With Orders
     *
     * @param patientQueueList
     * @return
     */
    public List<PharmacyMapper> mapPatientQueueToMapperWithDrugOrders(List<PatientQueue> patientQueueList, boolean includeOrders);

    /**
     * @param encounter
     * @return
     */
    Provider getProviderFromEncounter(Encounter encounter);

    /**
     * @param query
     * @param encounterId
     * @param includeProccesed
     * @return
     * @throws ParseException
     * @throws IOException
     */
    public SimpleObject getOrderResultsOnEncounter(String query, int encounterId, boolean includeProccesed)
            throws ParseException, IOException;

    /**
     * @param encounter
     * @param locationTo
     * @return
     * @throws ParseException
     */
    public boolean patientQueueExists(Encounter encounter, Location locationTo, Location locationFrom, PatientQueue.Status status) throws ParseException;

    /**
     * Complete Previous Queue of Patient
     * @param patient
     * @param location
     * @param searchStatus
     * @return
     */
    public PatientQueue completePreviousQueue(Patient patient, Location location, PatientQueue.Status searchStatus);


    /**
     * @param patient
     * @param location
     * @return
     */
    public PatientQueue getPreviousQueue(Patient patient, Location location, PatientQueue.Status status);


    /**
     * This Method completes all facility out patient active patient visits found.
     * @param patient the patient whose visits are to be completed
     */
    public void completePatientActiveVisit(Patient patient);

    /**
     * This Method creates a patient Program Attribute for a given patient in a given program
     * @param programAttributeType the programAttribute Type which will be created
     * @param patientProgram The Patient Program where the program attribute will be added to
     * @param value the value of the Program attribute
     * @return
     */
    public PatientProgramAttribute generatePatientProgramAttribute(ProgramAttributeType programAttributeType, PatientProgram patientProgram, String value);

    /**
     * This creates program attribute from an observation list
     * @param patientProgram the patient program where the programAttribute will be added
     * @param observations an observation list where the programAttribute will be generated
     * @param conceptID the conceptId which will bw used to match the target observation
     * @param programAttributeUUID the uuid of the programAttribute to be created.
     * @return
     */
    public PatientProgramAttribute generatePatientProgramAttributeFromObservation(PatientProgram patientProgram, Set<Obs> observations, Integer conceptID, String programAttributeUUID);

    /**
     * This Method Checks if a test order has results entered on it either through an encounter or on the order it self
     * @param order the order which is being checked
     * @return true is the order has results and false if it doesnt have results
     */
    public boolean testOrderHasResults(Order order);


    /**
     * Update or Save orderObs. Requires a orderObs
     * @param orderObs The OrderObs to be saved
     * @return OrderObs that has been saved
     * @throws APIException
     */
    @Transactional
    public OrderObs saveOrderObs(OrderObs orderObs);

    /**
     * Get OrderObs List By search Params
     *
     * @param encounter The encounter where order(s) were created
     * @param onOrBefore The date when order(s) were created
     * @param onOrAfter The date when order(s) were created
     * @param orders The order to search in the list
     * @param obs The obs to search in the list
     * @return List<OrderObs> A list of orderObs that meet the parameters
     */
    @Transactional(readOnly = true)
    public List<OrderObs> getOrderObs(Encounter encounter,  Date onOrBefore, Date onOrAfter,  List<Order> orders, List<Obs> obs, boolean includeVoided);

    /**
     * Get OrderObs List By search Obs
     * @param obs The obs to search in the list
     * @return OrderObs A list of orderObs that meet the parameters
     */
    @Transactional(readOnly = true)
    public OrderObs getOrderObsByObs(Obs obs);


    /**
     * Gets all OrderObs
     * @return List<OrderObs
     */
    @Transactional(readOnly = true)
    public List<OrderObs> getAllOrderObs();

    /**
     * Get OrderObs List By search Obs
     * @param orderObsId The obs to search in the list
     * @return OrderObs A list of orderObs that meet the parameters
     */
    @Transactional(readOnly = true)
    public OrderObs getOrderObsById(Integer orderObsId);

    /**
     * Get OrderObs List By search Obs
     * @param uuid The obs to search in the list
     * @return OrderObs A list of orderObs that meet the parameters
     */
    @Transactional(readOnly = true)
    public OrderObs getOrderObsByUuid(String uuid);


    /**
     * Get OrderObs List By search Obs
     * @param order The obs to search in the list
     * @return OrderObs A list of orderObs that meet the parameters
     */
    @Transactional(readOnly = true)
    public OrderObs getOrderObsByOrder(Order order);

    /**
     * send patient back to previous location from Laboratory
     * @param encounter
     * @param locationTo
     * @param locationFrom
     * @param previousQueueStatus
     * @return a new queue where the patient has been sent
     */
    public PatientQueue sendPatientBackToClinician(Encounter encounter, Location locationTo, Location locationFrom, String previousQueueStatus);

    /**
     * Generates LabNumber for the day for the patient
     * @param orderUuid the uuid of the order to be generated a lab number
     * @return generated lab number as a string
     */
    public String generateLabNumber(String orderUuid);


    /**
     * Supports the accession of a lab order
     * @param orderUuid the id of the order to be accessioned
     * @param accessionNumber the lab number or the accession number for the test
     * @param specimenSourceUuid the specimen source uuid for the order
     * @param instructions more instructions for the order
     */
    public TestOrder accessionLabTest(String orderUuid, String accessionNumber, String specimenSourceUuid, String instructions);

    public Map initializeMetaData();


    public void importMetaDataFromXMLFiles();

    public void installCommonMetadata();

    public void removeOldChangeLocksForDataIntegrityModule();

    public void generateOpenMRSIdentifierForPatientsWithout();

    public void initializePrimaryIdentifierTypeMapping();

    public void setHealthFacilityLocation();

    public void setFlagStatus();

    public List<Initializer> initialiseForms();

    public void disableEnableAPPS();

    public CheckInPatient checkInPatient(Patient patient, Location currentLocation, Location locationTo, Location queueRoom, Provider provider, String visitComment, String patientStatus, String visitTypeUuid,Integer priority);

    public void downloadFormsAndMetaDataFromGitHub();

    public void downloadFrontendFromGitHub();
    public void downloadOmodsFromGitHub();


}