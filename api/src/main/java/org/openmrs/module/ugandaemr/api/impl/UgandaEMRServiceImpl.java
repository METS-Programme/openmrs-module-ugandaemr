package org.openmrs.module.ugandaemr.api.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.openmrs.CareSetting;
import org.openmrs.Concept;
import org.openmrs.ConceptNumeric;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.EncounterRole;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientProgram;
import org.openmrs.PatientProgramAttribute;
import org.openmrs.ProgramAttributeType;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.TestOrder;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.User;
import org.openmrs.api.*;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.dataexchange.DataImporter;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.utils.MetadataUtil;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.metadatamapping.MetadataTermMapping;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.openmrs.module.patientqueueing.api.PatientQueueingService;
import org.openmrs.module.patientqueueing.mapper.PatientQueueMapper;
import org.openmrs.module.patientqueueing.model.PatientQueue;
import org.openmrs.module.stockmanagement.api.dto.DispenseRequest;
import org.openmrs.module.ugandaemr.PublicHoliday;
import org.openmrs.module.ugandaemr.UgandaEMRConstants;
import org.openmrs.module.ugandaemr.activator.AppConfigurationInitializer;
import org.openmrs.module.ugandaemr.activator.HtmlFormsInitializer;
import org.openmrs.module.ugandaemr.activator.Initializer;
import org.openmrs.module.ugandaemr.activator.JsonFormsInitializer;
import org.openmrs.module.ugandaemr.api.deploy.bundle.CommonMetadataBundle;
import org.openmrs.module.ugandaemr.api.deploy.bundle.UgandaAddressMetadataBundle;
import org.openmrs.module.ugandaemr.api.deploy.bundle.UgandaEMRPatientFlagMetadataBundle;
import org.openmrs.module.ugandaemr.api.queuemapper.CheckInPatient;
import org.openmrs.module.ugandaemr.api.queuemapper.Identifier;
import org.openmrs.module.ugandaemr.api.queuemapper.PatientQueueVisitMapper;
import org.openmrs.module.ugandaemr.api.UgandaEMRService;
import org.openmrs.module.ugandaemr.api.db.UgandaEMRDAO;
import org.openmrs.module.ugandaemr.api.lab.OrderObs;
import org.openmrs.module.ugandaemr.api.lab.mapper.LabQueueMapper;
import org.openmrs.module.ugandaemr.api.lab.mapper.OrderMapper;
import org.openmrs.module.ugandaemr.api.lab.util.LaboratoryUtil;
import org.openmrs.module.ugandaemr.api.lab.util.TestResultModel;
import org.openmrs.module.ugandaemr.metadata.core.location.LocationOrganization;
import org.openmrs.module.ugandaemr.metadata.core.PatientIdentifierTypes;
import org.openmrs.module.ugandaemr.pharmacy.DispensingModelWrapper;
import org.openmrs.module.ugandaemr.pharmacy.mapper.DrugOrderMapper;
import org.openmrs.module.ugandaemr.pharmacy.mapper.PharmacyMapper;
import org.openmrs.notification.Alert;
import org.openmrs.notification.AlertService;
import org.openmrs.order.OrderUtil;
import org.openmrs.parameter.EncounterSearchCriteria;
import org.openmrs.parameter.EncounterSearchCriteriaBuilder;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.resource.ResourceFactory;
import org.openmrs.ui.framework.resource.ResourceProvider;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.module.stockmanagement.api.StockManagementService;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.DirectoryStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.nio.charset.StandardCharsets;

import static org.openmrs.OrderType.TEST_ORDER_TYPE_UUID;
import static org.openmrs.module.ugandaemr.UgandaEMRConstants.*;
import static org.openmrs.module.ugandaemr.metadata.core.EncounterTypes.TRANSFER_IN;
import static org.openmrs.module.ugandaemr.metadata.core.EncounterTypes.TRANSFER_OUT;

public class UgandaEMRServiceImpl extends BaseOpenmrsService implements UgandaEMRService {

    protected final Log log = LogFactory.getLog(this.getClass());

    private UgandaEMRDAO dao;

    /**
     * @param dao the dao to set
     */
    public void setDao(UgandaEMRDAO dao) {
        this.dao = dao;
    }

    /**
     * @return the dao
     */
    public UgandaEMRDAO getDao() {
        return dao;
    }

    private String ordersListLabel = "ordersList";

    @Override
    public void linkExposedInfantToMotherViaARTNumber(Person infant, String motherARTNumber) {

        PatientService patientService = Context.getPatientService();
        PersonService personService = Context.getPersonService();
        log.debug("Linking infant with ID " + infant.getPersonId() + " to mother with ART Number " + motherARTNumber);
        List<PatientIdentifierType> artNumberPatientidentifierTypes = new ArrayList<>();
        artNumberPatientidentifierTypes.add(Context.getPatientService().getPatientIdentifierTypeByUuid(PatientIdentifierTypes.ART_PATIENT_NUMBER.uuid()));
        artNumberPatientidentifierTypes.add(Context.getPatientService().getPatientIdentifierTypeByUuid(PatientIdentifierTypes.HIV_CARE_NUMBER.uuid()));
        // find the mother by identifier
        List<Patient> mothers = patientService.getPatients(null, // name of the person
                motherARTNumber, //mother ART number
                artNumberPatientidentifierTypes, // ART Number and HIV Clinic number
                true); // match Identifier exactly
        if (mothers.size() != 0) {
            Person potentialMother = mothers.get(0).getPerson();
            // mothers have to be female and above 12 years of age
            if (potentialMother.getAge() != null && potentialMother.getAge() > 12 & potentialMother.getGender().equals("F")) {
                Relationship relationship = new Relationship();
                relationship.setRelationshipType(personService.getRelationshipTypeByUuid("8d91a210-c2cc-11de-8d13-0010c6dffd0f"));
                relationship.setPersonA(potentialMother);
                relationship.setPersonB(infant);
                personService.saveRelationship(relationship);
                log.debug("Infant with ID " + infant.getPersonId() + " linked to mother with ID " + potentialMother.getPersonId());
            }
        }
    }

    @Override
    public void linkExposedInfantToMotherViaARTNumber(Patient infant, String motherARTNumber) {
        linkExposedInfantToMotherViaARTNumber(infant.getPerson(), motherARTNumber);
    }

    public void setAlertForAllUsers(String alertMessage) {
        List<User> userList = Context.getUserService().getAllUsers();
        Alert alert = new Alert();
        for (User user : userList) {
            alert.addRecipient(user);
        }
        alert.setText(alertMessage);
        Context.getAlertService().saveAlert(alert);
    }

    /**
     * @see org.openmrs.module.ugandaemr.api.UgandaEMRService#generatePatientUIC(org.openmrs.Patient)
     */
    @Override
    public String generatePatientUIC(Patient patient) {
        String familyNameCode = "";
        String givenNameCode = "";
        String middleNameCode = "";
        String countryCode = "";
        String genderCode = "";
        Date dob = patient.getBirthdate();

        if (dob == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(dob);
        String monthCode = "";
        String year = (cal.get(Calendar.YEAR) + "").substring(2, 4);


        if (cal.get(Calendar.MONTH) <= 8) {
            monthCode = "0" + (cal.get(Calendar.MONTH) + 1);
        } else {
            monthCode = "" + (cal.get(Calendar.MONTH) + 1);
        }

        if (patient.getGender().equals("F")) {
            genderCode = "2";
        } else {
            genderCode = "1";
        }

        if (patient.getPerson().getPersonAddress() != null && !patient.getPerson().getPersonAddress().getCountry().isEmpty()) {
            countryCode = patient.getPerson().getPersonAddress().getCountry().substring(0, 2).toUpperCase();
        } else {
            countryCode = "XX";
        }

        if (patient.getFamilyName() != null && !patient.getFamilyName().isEmpty()) {
            String firstLetter = replaceLettersWithNumber(patient.getFamilyName().substring(0, 1));
            String secondLetter = replaceLettersWithNumber(patient.getFamilyName().substring(1, 2));
            String thirdLetter = replaceLettersWithNumber(patient.getFamilyName().substring(2, 3));
            familyNameCode = firstLetter + secondLetter + thirdLetter;
        } else {
            familyNameCode = "X";
        }

        if (patient.getGivenName() != null && !patient.getGivenName().isEmpty()) {
            String firstLetter = replaceLettersWithNumber(patient.getGivenName().substring(0, 1));
            String secondLetter = replaceLettersWithNumber(patient.getGivenName().substring(1, 2));
            String thirdLetter = replaceLettersWithNumber(patient.getGivenName().substring(2, 3));
            givenNameCode = firstLetter + secondLetter + thirdLetter;
        } else {
            givenNameCode = "X";
        }

        if (patient.getMiddleName() != null && !patient.getMiddleName().isEmpty()) {
            middleNameCode = replaceLettersWithNumber(patient.getMiddleName().substring(0, 1));
        } else {
            middleNameCode = "X";
        }


        return countryCode + "-" + monthCode + year + "-" + genderCode + "-" + givenNameCode + familyNameCode + middleNameCode;
    }

    /**
     * @see org.openmrs.module.ugandaemr.api.UgandaEMRService#generateAndSaveUICForPatientsWithOut()
     */
    @Override
    public void generateAndSaveUICForPatientsWithOut() {
        PatientService patientService = Context.getPatientService();
        List list = Context.getAdministrationService().executeSQL("select patient.patient_id from patient where patient_id NOT IN(select patient.patient_id from patient inner join patient_identifier pi on (patient.patient_id = pi.patient_id)  inner join patient_identifier_type pit on (pi.identifier_type = pit.patient_identifier_type_id) where pit.uuid='877169c4-92c6-4cc9-bf45-1ab95faea242')", true);
        PatientIdentifierType patientIdentifierType = patientService.getPatientIdentifierTypeByUuid("877169c4-92c6-4cc9-bf45-1ab95faea242");
        for (Object object : list) {
            Integer patientId = (Integer) ((ArrayList) object).get(0);
            Patient patient = patientService.getPatient(patientId);

            String uniqueIdentifierCode = generatePatientUIC(patient);

            if (uniqueIdentifierCode != null) {
                PatientIdentifier patientIdentifier = new PatientIdentifier();
                patientIdentifier.setIdentifier(uniqueIdentifierCode);
                patientIdentifier.setIdentifierType(patientIdentifierType);
                patientIdentifier.setLocation(Context.getLocationService().getLocationByUuid(LocationOrganization.PARENT.uuid()));
                patientIdentifier.setCreator(Context.getUserService().getUser(1));
                patientIdentifier.setPreferred(false);
                patientIdentifier.setDateCreated(new Date());
                patientIdentifier.setPatient(patient);
                try {
                    patientService.savePatientIdentifier(patientIdentifier);
                } catch (Exception e) {
                    log.error("Failed to Save UIC for patient #" + patient.getPatientId(), e);
                }

            }
        }
    }

    /**
     * This Method replaces letters with number position in the alphabet
     *
     * @param letter the alphabetical letter
     * @return number that matches the alphabetical letter position
     */
    private String replaceLettersWithNumber(String letter) {
        String numberToReturn = "X";

        switch (letter.toUpperCase()) {
            case "A":
                numberToReturn = "01";
                break;
            case "B":
                numberToReturn = "02";
                break;
            case "C":
                numberToReturn = "03";
                break;
            case "D":
                numberToReturn = "04";
                break;
            case "E":
                numberToReturn = "05";
                break;
            case "F":
                numberToReturn = "06";
                break;
            case "G":
                numberToReturn = "07";
                break;
            case "H":
                numberToReturn = "08";
                break;
            case "I":
                numberToReturn = "09";
                break;
            case "J":
                numberToReturn = "10";
                break;
            case "K":
                numberToReturn = "11";
                break;
            case "L":
                numberToReturn = "12";
                break;
            case "M":
                numberToReturn = "13";
                break;
            case "N":
                numberToReturn = "14";
                break;
            case "O":
                numberToReturn = "15";
                break;
            case "P":
                numberToReturn = "16";
                break;
            case "Q":
                numberToReturn = "17";
                break;
            case "R":
                numberToReturn = "18";
                break;
            case "S":
                numberToReturn = "19";
                break;
            case "T":
                numberToReturn = "20";
                break;
            case "U":
                numberToReturn = "21";
                break;
            case "V":
                numberToReturn = "22";
                break;
            case "W":
                numberToReturn = "23";
                break;
            case "X":
                numberToReturn = "24";
                break;
            case "Y":
                numberToReturn = "25";
                break;
            case "Z":
                numberToReturn = "26";
                break;

            default:
                numberToReturn = "X";
        }
        return numberToReturn;
    }

    /**
     * @see org.openmrs.module.ugandaemr.api.UgandaEMRService#stopActiveOutPatientVisits()
     */
    public void stopActiveOutPatientVisits() {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        SimpleDateFormat formatterExt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String currentDate = formatterExt.format(OpenmrsUtil.firstSecondOfDay(new Date()));

        //TODO Change AdministrationService to Autowired
        AdministrationService administrationService = Context.getAdministrationService();

        String visitTypeUUID = administrationService.getGlobalProperty("ugandaemr.autoCloseVisit.visitTypeUUID");

        VisitService visitService = Context.getVisitService();

        List activeVisitList = null;
        activeVisitList = administrationService.executeSQL("select visit.visit_id from visit inner join visit_type on (visit.visit_type_id = visit_type.visit_type_id)  where visit_type.uuid='" + visitTypeUUID + "' AND visit.date_stopped IS NULL AND  visit.date_started < '" + currentDate + "'", true);

        for (Object object : activeVisitList) {
            ArrayList<Integer> integers = (ArrayList) object;
            Visit visit = visitService.getVisit(integers.get(0));
            try {
                Date largestEncounterDate = OpenmrsUtil.getLastMomentOfDay(visit.getStartDatetime());

                for (Encounter encounter : visit.getEncounters()) {
                    if (encounter.getEncounterDatetime().after(largestEncounterDate)) {
                        largestEncounterDate = OpenmrsUtil.getLastMomentOfDay(encounter.getEncounterDatetime());
                    }
                }
                visitService.endVisit(visit, OpenmrsUtil.getLastMomentOfDay(largestEncounterDate));
            } catch (Exception e) {
                log.error("Failed to auto close visit", e);
            }

        }
    }

    /**
     * @see org.openmrs.module.ugandaemr.api.UgandaEMRService#transferredOut(org.openmrs.Patient, java.util.Date)
     */
    @Override
    public Map transferredOut(Patient patient, Date date) {
        Map map = new HashMap();
        EncounterService encounterService = Context.getEncounterService();
        List<EncounterType> encounterTypes = encounterService.findEncounterTypes(TRANSFER_OUT.name());

        EncounterSearchCriteria encounterSearchCriteria = new EncounterSearchCriteriaBuilder().setPatient(patient).setIncludeVoided(false).setEncounterTypes(encounterTypes).setFromDate(date).createEncounterSearchCriteria();

        List<Encounter> encounters = encounterService.getEncounters(encounterSearchCriteria);

        Collections.reverse(encounters);
        if (encounters.size() > 0) {
            Encounter encounter = encounters.get(0);
            if (encounters.get(0).getEncounterType() == Context.getEncounterService().getEncounterType(TRANSFER_OUT.name())) {
                List<Encounter> encounters1 = new ArrayList<>();
                List<Concept> transferOutPlaceConceptList = new ArrayList<>();
                transferOutPlaceConceptList.add(Context.getConceptService().getConcept(UgandaEMRConstants.TRANSFER_OUT_PLACE_CONCEPT_ID));
                encounters1.add(encounter);
                map.put(PATIENT_TRANSERRED_OUT, true);
                map.put(PATIENT_TRANSFERED_OUT_DATE, encounter.getEncounterDatetime());
                List<Person> people = new ArrayList<>();
                people.add(patient.getPerson());
                List<Obs> obsList = Context.getObsService().getObservations(people, encounters, transferOutPlaceConceptList, null, null, null, null, 1, null, null, null, false);
                if (obsList.size() > 0) {
                    map.put(PATIENT_TRANSFERED_OUT_LOCATION, obsList.get(0).getValueText());
                }
            } else {
                map.put(PATIENT_TRANSERRED_OUT, false);
            }
        } else {
            map.put(PATIENT_TRANSERRED_OUT, false);
        }
        return map;
    }

    /**
     * @see org.openmrs.module.ugandaemr.api.UgandaEMRService#transferredIn(org.openmrs.Patient, java.util.Date)
     */
    @Override
    public Map transferredIn(Patient patient, Date date) {
        Map map = new HashMap();

        EncounterService encounterService = Context.getEncounterService();

        Collection<EncounterType> encounterTypes = encounterService.findEncounterTypes(TRANSFER_IN.name());

        EncounterSearchCriteria encounterSearchCriteria = new EncounterSearchCriteriaBuilder().setPatient(patient).setIncludeVoided(false).setEncounterTypes(encounterTypes).setFromDate(date).createEncounterSearchCriteria();

        List<Encounter> encounters = encounterService.getEncounters(encounterSearchCriteria);

        if (encounters.size() > 0) {
            map.put(PATIENT_TRANSERRED_IN, true);
            List<Concept> transferInPlaceConceptList = new ArrayList<>();
            List<Person> people = new ArrayList<>();
            people.add(patient.getPerson());
            transferInPlaceConceptList.add(Context.getConceptService().getConcept(TRANSFER_IN_FROM_PLACE_CONCEPT_ID));
            List<Obs> obsList = Context.getObsService().getObservations(people, encounters, transferInPlaceConceptList, null, null, null, null, 1, null, null, null, false);
            if (obsList.size() > 0) {
                map.put(PATIENT_TRANSFERED_IN_LOCATION, obsList.get(0).getValueText());
                map.put(PATIENT_TRANSFERED_IN_DATE, obsList.get(0).getEncounter().getEncounterDatetime());
            } else {
                map.put(PATIENT_TRANSFERED_IN_DATE, encounters.get(0).getEncounterDatetime());
            }
        } else {
            map.put(PATIENT_TRANSERRED_IN, false);
        }
        return map;
    }

    public boolean isTransferredOut(Patient patient, Date date) {
        return (boolean) transferredOut(patient, date).get(PATIENT_TRANSERRED_OUT);
    }

    @Override
    public boolean isTransferredIn(Patient patient, Date date) {
        return (boolean) transferredOut(patient, date).get(PATIENT_TRANSERRED_OUT);
    }

    @Override
    public List<Encounter> getTransferHistory(Patient patient) {

        EncounterService encounterService = Context.getEncounterService();
        Collection<EncounterType> encounterTypes = new ArrayList<>();

        encounterTypes.add(encounterService.getEncounterTypeByUuid(TRANSFER_IN.uuid()));
        encounterTypes.add(encounterService.getEncounterTypeByUuid(TRANSFER_OUT.uuid()));

        EncounterSearchCriteria encounterSearchCriteria = new EncounterSearchCriteriaBuilder().setPatient(patient).setIncludeVoided(false).setEncounterTypes(encounterTypes).createEncounterSearchCriteria();

        List<Encounter> encounters = encounterService.getEncounters(encounterSearchCriteria);

        return encounters;
    }

    @Override
    public List<PublicHoliday> getAllPublicHolidays() throws APIException {
        return dao.getAllPublicHolidays();
    }

    @Override
    public PublicHoliday getPublicHolidayByDate(Date publicHolidayDate) throws APIException {
        return dao.getPublicHolidayByDate(publicHolidayDate);
    }

    @Override
    public PublicHoliday savePublicHoliday(PublicHoliday publicHoliday) {
        return dao.savePublicHoliday(publicHoliday);
    }

    @Override
    public PublicHoliday getPublicHolidaybyUuid(String uuid) {
        return dao.getPublicHolidaybyUuid(uuid);
    }

    @Override
    public List<PublicHoliday> getPublicHolidaysByDate(Date publicHolidayDate) throws APIException {
        return dao.getPublicHolidaysByDate(publicHolidayDate);
    }

    /**
     * @see org.openmrs.module.ugandaemr.api.UgandaEMRService#createPatientHIVSummaryEncounterOnTransferIn(org.openmrs.module.htmlformentry.FormEntrySession)
     */
    public Encounter createPatientHIVSummaryEncounterOnTransferIn(FormEntrySession formEntrySession) {
        ConceptService conceptService = Context.getConceptService();
        EncounterService encounterService = Context.getEncounterService();
        Encounter encounter;

        if (hasHIVSummaryPage(formEntrySession.getPatient(), "8d5b27bc-c2cc-11de-8d13-0010c6dffd0f") == null) {
            encounter = new Encounter();
            encounter.setEncounterType(encounterService.getEncounterTypeByUuid("8d5b27bc-c2cc-11de-8d13-0010c6dffd0f"));
            encounter.setLocation(formEntrySession.getEncounter().getLocation());
            encounter.setPatient(formEntrySession.getPatient());
            encounter.setEncounterDatetime(new Date());
            encounter.setForm(Context.getFormService().getFormByUuid("52653a60-8300-4c13-be4d-4b746da06fee"));


            //*ART Start in information or Baseline Information//.
            Obs baselineRegimenObsGroup = createNewObs(conceptService.getConcept(99162), encounter);
            encounter.addObs(baselineRegimenObsGroup);

            Obs artStartDate = generateObsFromObs(formEntrySession.getEncounter().getAllObs(), 99161, 99161, encounter);
            baselineRegimenObsGroup.addGroupMember(artStartDate);
            encounter.addObs(artStartDate);

            Obs artStartRegimen = generateObsFromObs(formEntrySession.getEncounter().getAllObs(), 99061, 99061, encounter);
            baselineRegimenObsGroup.addGroupMember(artStartRegimen);
            encounter.addObs(artStartRegimen);

            Obs baselineWeight = generateObsFromObs(formEntrySession.getEncounter().getAllObs(), 99069, 99069, encounter);
            baselineRegimenObsGroup.addGroupMember(baselineWeight);
            encounter.addObs(baselineWeight);

            Obs baselineCD4 = generateObsFromObs(formEntrySession.getEncounter().getAllObs(), 99071, 99071, encounter);
            baselineRegimenObsGroup.addGroupMember(baselineCD4);
            encounter.addObs(baselineCD4);

            Obs baselineWHOClinicStage = generateObsFromObs(formEntrySession.getEncounter().getAllObs(), 163026, 99070, encounter);
            baselineRegimenObsGroup.addGroupMember(baselineWHOClinicStage);
            encounter.addObs(baselineWHOClinicStage);

        } else {
            encounter = hasHIVSummaryPage(formEntrySession.getPatient(), "8d5b27bc-c2cc-11de-8d13-0010c6dffd0f");
        }

        //*Transfer in information//.
        Obs transferInObsGroup = createNewObs(conceptService.getConcept(99065), encounter);
        encounter.addObs(transferInObsGroup);

        Obs transferInDate = createNewObs(conceptService.getConcept(99160), encounter);
        transferInDate.setValueDate(new Date());
        transferInObsGroup.addGroupMember(transferInDate);
        encounter.addObs(transferInDate);

        Obs transferInFromObs = generateObsFromObs(formEntrySession.getEncounter().getAllObs(), 99109, 90206, encounter);
        transferInObsGroup.addGroupMember(transferInFromObs);
        encounter.addObs(transferInFromObs);

        Obs transferInRegimen = generateObsFromObs(formEntrySession.getEncounter().getAllObs(), 90315, 99064, encounter);
        transferInObsGroup.addGroupMember(transferInRegimen);
        encounter.addObs(transferInRegimen);


        encounterService.saveEncounter(encounter);

        return encounter;
    }


    /**
     * @see org.openmrs.module.ugandaemr.api.UgandaEMRService#hasHIVSummaryPage(org.openmrs.Patient, java.lang.String)
     */
    public Encounter hasHIVSummaryPage(Patient patient, String encounterTypeUUID) {
        EncounterService encounterService = Context.getEncounterService();
        Collection<EncounterType> encounterTypes = new ArrayList<>();
        encounterTypes.add(encounterService.getEncounterTypeByUuid(encounterTypeUUID));
        EncounterSearchCriteria encounterSearchCriteria = new EncounterSearchCriteriaBuilder().setPatient(patient).setIncludeVoided(false).setEncounterTypes(encounterTypes).createEncounterSearchCriteria();
        List<Encounter> encounters = encounterService.getEncounters(encounterSearchCriteria);
        if (!encounters.isEmpty()) {
            for (Encounter encounter : encounters) {
                if (encounterTypeUUID.equals(encounter.getEncounterType().getUuid())) {
                    return encounter;
                }
            }
        }

        return null;
    }


    /**
     * @see org.openmrs.module.ugandaemr.api.UgandaEMRService#generateObsFromObs(java.util.Set, java.lang.Integer, java.lang.Integer, org.openmrs.Encounter)
     */
    public Obs generateObsFromObs(Set<Obs> observations, Integer lookUpConceptId, Integer conceptIDForNewObs, Encounter encounter) {
        for (Obs obs : observations) {
            if (lookUpConceptId.equals(obs.getConcept().getConceptId())) {
                Obs newObs = createNewObs(Context.getConceptService().getConcept(conceptIDForNewObs), encounter);
                newObs.setValueBoolean(obs.getValueBoolean());
                newObs.setValueCoded(obs.getValueCoded());
                newObs.setValueDate(obs.getValueDate());
                newObs.setValueDatetime(obs.getValueDatetime());
                newObs.setValueNumeric(obs.getValueNumeric());
                newObs.setValueText(obs.getValueText());
                return newObs;
            }
        }
        return null;
    }

    /**
     * @see org.openmrs.module.ugandaemr.api.UgandaEMRService#createNewObs(org.openmrs.Concept, org.openmrs.Encounter)
     */
    public Obs createNewObs(Concept concept, Encounter encounter) {
        Obs obs = new Obs();
        obs.setObsDatetime(encounter.getEncounterDatetime());
        obs.setPerson(encounter.getPatient());
        obs.setLocation(encounter.getLocation());
        obs.setEncounter(encounter);
        obs.setConcept(concept);
        return obs;
    }

    @Override
    public List<PatientQueueVisitMapper> mapPatientQueueToMapper(List<PatientQueue> patientQueueList) {
        List<PatientQueueVisitMapper> patientQueueMappers = new ArrayList<>();

        for (PatientQueue patientQueue : patientQueueList) {
            String names = patientQueue.getPatient().getFamilyName() + " " + patientQueue.getPatient().getGivenName() + " " + patientQueue.getPatient().getMiddleName();
            PatientQueueVisitMapper patientQueueVisitMapper = new PatientQueueVisitMapper();
            patientQueueVisitMapper.setId(patientQueue.getId());
            patientQueueVisitMapper.setPatientQueueUuid(patientQueue.getUuid());
            patientQueueVisitMapper.setPatientNames(names.replace("null", ""));
            patientQueueVisitMapper.setPatientId(patientQueue.getPatient().getPatientId());
            patientQueueVisitMapper.setLocationFrom(patientQueue.getLocationFrom().getName());
            patientQueueVisitMapper.setLocationTo(patientQueue.getLocationTo().getName());
            patientQueueVisitMapper.setVisitNumber(patientQueue.getVisitNumber());

            if (patientQueue.getProvider() != null) {
                patientQueueVisitMapper.setProviderNames(patientQueue.getProvider().getName());
            }

            if (patientQueue.getCreator() != null) {
                patientQueueVisitMapper.setCreatorNames(patientQueue.getCreator().getDisplayString());
            }

            if (patientQueue.getEncounter() != null) {
                patientQueueVisitMapper.setEncounterId(patientQueue.getEncounter().getEncounterId().toString());
            }

            if (patientQueue.getStatus() == PatientQueue.Status.PENDING && patientQueue.getLocationFrom().getUuid().equals(LAB_LOCATION_UUID)) {
                patientQueueVisitMapper.setStatus(QUEUE_STATUS_FROM_LAB);
            } else {
                patientQueueVisitMapper.setStatus(patientQueue.getStatus().name());
            }

            Visit visit = getPatientCurrentVisit(patientQueue.getPatient());
            if (visit != null) {
                patientQueueVisitMapper.setVisitId(visit.getVisitId());
            }


            patientQueueVisitMapper.setAge(patientQueue.getPatient().getAge().toString());
            patientQueueVisitMapper.setGender(patientQueue.getPatient().getGender());
            patientQueueVisitMapper.setDateCreated(patientQueue.getDateCreated().toString());
            if (patientQueue.getDateChanged() != null) {
                patientQueueVisitMapper.setDateChanged(patientQueue.getDateChanged().toString());
            }

            List<Identifier> identifiers = new ArrayList<>();
            for (PatientIdentifier patientIdentifier : patientQueue.getPatient().getIdentifiers()) {
                Identifier identifier = new Identifier(patientIdentifier.getIdentifier(), patientIdentifier.getIdentifierType().getName(), patientIdentifier.getIdentifierType().getUuid());
                if (patientIdentifier.getLocation() != null) {
                    identifier.setIdentifierLocationUuid(patientIdentifier.getLocation().getUuid());
                }
                identifiers.add(identifier);
            }
            patientQueueVisitMapper.setPatientIdentifier(identifiers);

            patientQueueMappers.add(patientQueueVisitMapper);
        }
        return patientQueueMappers;
    }


    /**
     * Checks if Sample ID genereated is already issued out
     *
     * @param sampleId
     * @param orderNumber
     * @return
     * @throws ParseException
     */
    public boolean isSampleIdExisting(String sampleId, String orderNumber) throws ParseException {
        List list = Context.getAdministrationService().executeSQL(String.format("select * from orders where accession_number=\"%s\"", sampleId), true);
        boolean exists = false;
        if (!list.isEmpty()) {
            exists = true;
        }
        return exists;
    }

    /**
     * @param test
     * @return
     */
    public Set<TestResultModel> renderTests(Order test) {
        Set<TestResultModel> trms = new HashSet<>();
        if (test.getEncounter() != null) {
            Encounter encounter = test.getEncounter();
            for (Obs obs : encounter.getAllObs()) {
                if (obs.getOrder() != null) {
                    if (obs.hasGroupMembers()) {
                        for (Obs groupMemberObs : obs.getGroupMembers()) {
                            TestResultModel trm = new TestResultModel();
                            trm.setInvestigation(test.getConcept().getDisplayString());
                            trm.setTestId(obs.getOrder().getOrderId());
                            trm.setTestUuid(obs.getOrder().getUuid());
                            trm.setSet(obs.getConcept().getDisplayString());
                            trm.setOrderdate(obs.getOrder().getDateActivated());
                            trm.setConcept(obs.getConcept());
                            setTestResultModelValue(groupMemberObs, trm);
                            trms.add(trm);
                        }
                    } else if (obs.getObsGroup() == null) {
                        TestResultModel trm = new TestResultModel();
                        trm.setInvestigation(test.getConcept().getName().getName());
                        trm.setSet(test.getConcept().getDatatype().getName());
                        trm.setOrderdate(obs.getOrder().getDateActivated());
                        trm.setTestId(obs.getOrder().getOrderId());
                        trm.setTestUuid(obs.getOrder().getUuid());
                        trm.setConcept(obs.getConcept());
                        setTestResultModelValue(obs, trm);
                        trms.add(trm);
                    }
                }
            }
        }
        return trms;
    }

    /**
     * Process Lab Orders
     *
     * @param query
     * @param asOfDate
     * @return
     * @throws ParseException
     * @throws IOException
     */
    public SimpleObject getProcessedOrders(String query, Date asOfDate, boolean includeProccesed) throws ParseException, IOException {
        Date date;
        SimpleObject simpleObject = new SimpleObject();
        ObjectMapper objectMapper = new ObjectMapper();
        OrderService orderService = Context.getOrderService();

        if (asOfDate != null) {
            query = String.format(query, org.openmrs.module.ugandaemr.utils.DateFormatUtil.dateFormtterString(asOfDate, DAY_START_TIME), org.openmrs.module.ugandaemr.utils.DateFormatUtil.dateFormtterString(asOfDate, DAY_END_TIME));
        }


        List list = Context.getAdministrationService().executeSQL(query, true);


        Set<Order> unProcesedOrderList = new HashSet<>();

        Set<Order> proccesedOrderList = new HashSet<>();

        if (!list.isEmpty()) {
            for (Object o : list) {
                Order order = orderService.getOrder(Integer.parseUnsignedInt(((ArrayList) o).get(0).toString()));
                if (order.getAccessionNumber() == null) {
                    unProcesedOrderList.add(order);
                }
                proccesedOrderList.add(order);
            }
        }

        if (includeProccesed && !proccesedOrderList.isEmpty()) {
            simpleObject.put(ordersListLabel, objectMapper.writeValueAsString(processOrders(proccesedOrderList, true)));
        } else if (!unProcesedOrderList.isEmpty() && !includeProccesed) {
            simpleObject.put(ordersListLabel, objectMapper.writeValueAsString(processOrders(unProcesedOrderList, true)));
        }
        return simpleObject;
    }

    /**
     * Process Lab Orders
     *
     * @param query
     * @param encounterId
     * @return
     * @throws ParseException
     * @throws IOException
     */
    public SimpleObject getOrderResultsOnEncounter(String query, int encounterId, boolean includeProccesed) throws ParseException, IOException {
        SimpleObject simpleObject = new SimpleObject();
        ObjectMapper objectMapper = new ObjectMapper();
        OrderService orderService = Context.getOrderService();

        query = String.format(query, encounterId);

        List list = Context.getAdministrationService().executeSQL(query, true);
        Set<Order> unProcesedOrderList = new HashSet<>();

        Set<Order> proccesedOrderList = new HashSet<>();

        if (!list.isEmpty()) {
            for (Object o : list) {
                Order order = orderService.getOrder(Integer.parseUnsignedInt(((ArrayList) o).get(0).toString()));
                if (order.getAccessionNumber() == null) {
                    unProcesedOrderList.add(order);
                }
                proccesedOrderList.add(order);
            }
        }

        if (includeProccesed && !proccesedOrderList.isEmpty()) {
            simpleObject.put(ordersListLabel, objectMapper.writeValueAsString(processOrders(proccesedOrderList, true)));
        } else if (!unProcesedOrderList.isEmpty() && !includeProccesed) {
            simpleObject.put(ordersListLabel, objectMapper.writeValueAsString(processOrders(unProcesedOrderList, true)));
        }
        return simpleObject;
    }

    /**
     * Processes Orders from Encounter to Order Mapper
     *
     * @param orders
     * @return
     */

    public Set<OrderMapper> processOrders(Set<Order> orders, boolean fiterOutProccessed) {
        Set<OrderMapper> orderMappers = new HashSet<>();
        OrderType orderType = Context.getOrderService().getOrderTypeByUuid(ORDER_TYPE_LAB_UUID);
        orders = orders.stream().filter(order -> order.getOrderType().equals(orderType)).collect(Collectors.toSet());

        orders.forEach(order -> {
            String names = order.getPatient().getFamilyName() + " " + order.getPatient().getGivenName() + " " + order.getPatient().getMiddleName();
            OrderMapper orderMapper = new OrderMapper();
            orderMapper.setAccessionNumber(order.getAccessionNumber());
            orderMapper.setCareSetting(order.getCareSetting().getName());
            orderMapper.setConcept(order.getConcept().getConceptId().toString());
            orderMapper.setConceptName(order.getConcept().getDisplayString());
            orderMapper.setDateActivated(order.getDateActivated().toString());
            orderMapper.setOrderer(order.getOrderer().getName());
            orderMapper.setOrderNumber(order.getOrderNumber());
            orderMapper.setOrderUuid(order.getUuid());
            orderMapper.setPatientId(order.getPatient().getPatientId());
            orderMapper.setInstructions(order.getInstructions());
            orderMapper.setFulfillerComment(order.getFulfillerComment());
            if (order.getFulfillerStatus() != null) {
                orderMapper.setFulfillerStatus(order.getFulfillerStatus().name());
            }
            orderMapper.setUrgency(order.getUrgency().name());
            orderMapper.setPatient(names.replace("null", ""));
            orderMapper.setOrderId(order.getOrderId());
            orderMapper.setEncounterId(order.getEncounter().getEncounterId());
            orderMapper.setOrderClass(order.getConcept().getConceptClass().getName());
            if (order.isActive()) {
                orderMapper.setStatus(QUEUE_STATUS_ACTIVE);
            }

            if (testOrderHasResults(order)) {
                orderMapper.setStatus(QUEUE_STATUS_HAS_RESULTS);
            }
            orderMappers.add(orderMapper);
        });
        return orderMappers;
    }


    /**
     * Processes Orders from Encounter to Order Mapper
     *
     * @param orders
     * @return
     */

    public Set<DrugOrderMapper> processDrugOrders(Set<Order> orders) {
        Set<DrugOrderMapper> orderMappers = new HashSet<>();
        boolean enableStockManagement = Boolean.parseBoolean(Context.getAdministrationService().getGlobalProperty("ugandaemr.enableStockManagement"));
        OrderType orderType = Context.getOrderService().getOrderTypeByUuid(ORDER_TYPE_DRUG_UUID);
        orders = orders.stream().filter(order -> order.getOrderType().equals(orderType) && order.isActive()).collect(Collectors.toSet());

        orders.forEach(order -> {
            DrugOrder drugOrder = (DrugOrder) order;
            String names = order.getPatient().getFamilyName() + " " + order.getPatient().getGivenName() + " " + order.getPatient().getMiddleName();
            DrugOrderMapper drugOrderMapper = new DrugOrderMapper();
            drugOrderMapper.setAsNeeded(drugOrder.getAsNeeded());
            drugOrderMapper.setAsNeededCondition(drugOrder.getAsNeededCondition());
            drugOrderMapper.setBrandName(drugOrder.getBrandName());
            drugOrderMapper.setDose(drugOrder.getDose());
            drugOrderMapper.setDoseUnits(drugOrder.getDoseUnits().getDisplayString());
            drugOrderMapper.setDrug(drugOrder.getConcept().getDisplayString());
            drugOrderMapper.setDuration(drugOrder.getDuration());
            drugOrderMapper.setDurationUnits(drugOrder.getDurationUnits().getDisplayString());
            drugOrderMapper.setDrugNonCoded(drugOrder.getDrugNonCoded());
            drugOrderMapper.setFrequency(drugOrder.getFrequency().getName());
            drugOrderMapper.setNumRefills(drugOrder.getNumRefills());
            drugOrderMapper.setQuantity(drugOrder.getQuantity());
            drugOrderMapper.setQuantityUnits(drugOrder.getQuantityUnits().getDisplayString());
            drugOrderMapper.setStrength(getDrugStrength(drugOrder));
            drugOrderMapper.setRoute(drugOrder.getRoute().getDisplayString());
            drugOrderMapper.setAccessionNumber(drugOrder.getAccessionNumber());
            drugOrderMapper.setDosingInstructions(drugOrder.getDosingInstructions());
            drugOrderMapper.setCareSetting(drugOrder.getCareSetting().getName());
            drugOrderMapper.setConcept(drugOrder.getConcept().getConceptId().toString());
            drugOrderMapper.setConceptName(drugOrder.getConcept().getDisplayString());
            drugOrderMapper.setDateActivated(drugOrder.getDateActivated().toString());
            drugOrderMapper.setOrderer(drugOrder.getOrderer().getName());
            drugOrderMapper.setOrderNumber(drugOrder.getOrderNumber());
            drugOrderMapper.setPatientId(drugOrder.getPatient().getPatientId());
            drugOrderMapper.setInstructions(drugOrder.getInstructions());
            drugOrderMapper.setUrgency(drugOrder.getUrgency().name());

            drugOrderMapper.setDispensingLocation(getDispesingLocation(drugOrder));

            if (drugOrder.getDrug() != null) {
                drugOrderMapper.setDrugUUID(drugOrder.getDrug().getUuid());
            }

            drugOrderMapper.setPatient(names.replace("null", ""));
            drugOrderMapper.setOrderId(drugOrder.getOrderId());
            drugOrderMapper.setEncounterId(drugOrder.getEncounter().getEncounterId());
            if (order.isActive()) {
                drugOrderMapper.setStatus(QUEUE_STATUS_ACTIVE);
            }

            if (testOrderHasResults(order)) {
                drugOrderMapper.setStatus(QUEUE_STATUS_HAS_RESULTS);
            }
            orderMappers.add(drugOrderMapper);
        });
        return orderMappers;
    }


    /**
     * Get Medication Strength from the drug order
     *
     * @param drugOrder the drug order where the drug strength has to be picked
     * @return the
     */
    private String getDrugStrength(DrugOrder drugOrder) {
        List<Concept> concepts = new ArrayList<>();
        List<Encounter> encounters = new ArrayList<>();
        List<Person> personList = new ArrayList<>();
        personList.add(drugOrder.getPatient().getPerson());
        concepts.add(drugOrder.getConcept());
        encounters.add(drugOrder.getEncounter());
        String medicationStrength = "";
        List<Obs> obs = Context.getObsService().getObservations(personList, encounters, null, concepts, null, null, null, null, null, null, null, false);
        if (!obs.isEmpty()) {
            Set<Obs> groupMembers = obs.get(0).getObsGroup().getGroupMembers();
            for (Obs groupMember : groupMembers) {
                if (groupMember.getConcept().getConceptId() == MEDICATION_STRENGTH_CONCEPT_ID) {
                    medicationStrength = groupMember.getValueText();
                    return medicationStrength;
                }
            }
        }
        return medicationStrength;
    }

    /**
     * Set Results Model
     *
     * @param obs
     * @param trm
     */
    private void setTestResultModelValue(Obs obs, TestResultModel trm) {
        Concept concept = obs.getConcept();
        trm.setTest(obs.getConcept().getDisplayString());
        trm.setTest(obs.getConcept().getDisplayString());
        if (concept != null) {
            String datatype = concept.getDatatype().getName();
            if (datatype.equalsIgnoreCase("Text")) {
                trm.setValue(obs.getValueText());
            } else if (datatype.equalsIgnoreCase("Numeric")) {
                if (obs.getValueText() != null) {
                    trm.setValue(obs.getValueText());
                } else if (obs.getValueNumeric() != null) {
                    trm.setValue(obs.getValueNumeric().toString());
                }
                ConceptNumeric cn = Context.getConceptService().getConceptNumeric(concept.getConceptId());
                trm.setUnit(cn.getUnits());
                if (cn.getLowNormal() != null) trm.setLowNormal(cn.getLowNormal().toString());

                if (cn.getHiNormal() != null) trm.setHiNormal(cn.getHiNormal().toString());

                if (cn.getHiAbsolute() != null) {
                    trm.setHiAbsolute(cn.getHiAbsolute().toString());
                }

                if (cn.getHiCritical() != null) {
                    trm.setHiCritical(cn.getHiCritical().toString());
                }

                if (cn.getLowAbsolute() != null) {
                    trm.setLowAbsolute(cn.getLowAbsolute().toString());
                }

                if (cn.getLowCritical() != null) {
                    trm.setLowCritical(cn.getLowCritical().toString());
                }

            } else if (datatype.equalsIgnoreCase("Coded")) {
                trm.setValue(obs.getValueCoded().getName().getName());
            }
        }
    }


    /**
     * @see org.openmrs.module.ugandaemr.api.UgandaEMRService#testOrderHasResults(org.openmrs.Order)
     */
    public boolean testOrderHasResults(Order order) {
        boolean hasOrder = false;

        List list = Context.getAdministrationService().executeSQL("select obs_id from obs where order_id=" + order.getOrderId() + "", true);

        if (!list.isEmpty()) {
            hasOrder = true;
        } else if (resultsEnteredOnEncounter(order)) {

            hasOrder = true;
        }
        return hasOrder;
    }


    private boolean resultsEnteredOnEncounter(Order order) {

        boolean resultsEnteredOnEncounter = false;

        Set<Obs> allObs = order.getEncounter().getAllObs(false);
        for (Obs obs1 : allObs) {
            if (obs1.getConcept().getConceptId().equals(order.getConcept().getConceptId()) && (!obs1.getValueAsString(Locale.ENGLISH).equals("") || obs1.getValueAsString(Locale.ENGLISH) != null)) {
                resultsEnteredOnEncounter = true;
                return true;
            }
        }

        Set<Concept> conceptSet = allObs.stream().map(Obs::getConcept).collect(Collectors.toSet());
        List<Concept> members = order.getConcept().getSetMembers();

        if (members.size() > 0) {
            for (Concept concept : members) {
                if (conceptSet.contains(concept)) {
                    resultsEnteredOnEncounter = true;
                    return true;
                }
            }
        }

        return resultsEnteredOnEncounter;

    }


    /**
     * Add Lab Results Observation to Encounter
     *
     * @param encounter
     * @param testConcept
     * @param testGroupConcept
     * @param result
     * @param test
     */
    public void addLaboratoryTestObservation(Encounter encounter, Concept testConcept, Concept testGroupConcept, String result, Order test) {
        log.info("testConceptId=" + testConcept);
        log.info("testGroupConceptId=" + testGroupConcept);
        Obs obs = null;
        obs = getObs(encounter, testConcept, testGroupConcept);
        setObsAttributes(obs, encounter);
        obs.setConcept(testConcept);
        obs.setOrder(test);

        if (testConcept.getDatatype().getName().equalsIgnoreCase("Text")) {
            obs.setValueText(result);
        } else if (testConcept.getDatatype().getName().equalsIgnoreCase("Numeric")) {
            if (StringUtils.isNotBlank(result)) {
                obs.setValueNumeric(Double.parseDouble(result));
            }
        } else if (testConcept.getDatatype().getName().equalsIgnoreCase("Coded")) {
            Concept answerConcept = LaboratoryUtil.searchConcept(result);
            obs.setValueCoded(answerConcept);
        }
        if (testGroupConcept != null) {
            Obs testGroupObs = getObs(encounter, testGroupConcept, null);
            if (testGroupObs.getConcept() == null) {
                testGroupObs.setConcept(testGroupConcept);
                testGroupObs.setOrder(test);
                setObsAttributes(testGroupObs, encounter);
                encounter.addObs(testGroupObs);
            }
            log.info("Adding obs[concept=" + obs.getConcept() + ",uuid=" + obs.getUuid() + "] to obsgroup[concept=" + testGroupObs.getConcept() + ", uuid=" + testGroupObs.getUuid() + "]");
            testGroupObs.addGroupMember(obs);
        } else {
            encounter.addObs(obs);
        }

        log.warn("Obs size is: " + encounter.getObs().size());
    }

    /**
     * Convert PatientQueue List to PatientQueueMapping
     *
     * @param patientQueueList
     * @return
     */
    public List<PatientQueueMapper> mapPatientQueueToMapperWithOrders(List<PatientQueue> patientQueueList) {
        List<PatientQueueMapper> patientQueueMappers = new ArrayList<>();

        for (PatientQueue patientQueue : patientQueueList) {
            if (patientQueue.getEncounter() != null && !patientQueue.getEncounter().getOrders().isEmpty()) {
                String names = patientQueue.getPatient().getFamilyName() + " " + patientQueue.getPatient().getGivenName() + " " + patientQueue.getPatient().getMiddleName();
                LabQueueMapper labQueueMapper = new LabQueueMapper();
                labQueueMapper.setId(patientQueue.getId());
                labQueueMapper.setPatientNames(names.replace("null", ""));
                labQueueMapper.setPatientId(patientQueue.getPatient().getPatientId());
                labQueueMapper.setLocationFrom(patientQueue.getLocationFrom().getName());
                labQueueMapper.setLocationTo(patientQueue.getLocationTo().getName());
                labQueueMapper.setProviderNames(patientQueue.getProvider().getName());
                labQueueMapper.setStatus(patientQueue.getStatus().name());
                labQueueMapper.setAge(patientQueue.getPatient().getAge().toString());
                labQueueMapper.setDateCreated(patientQueue.getDateCreated().toString());
                if (patientQueue.getDateChanged() != null) {
                    labQueueMapper.setDateChanged(patientQueue.getDateChanged().toString());
                }
                labQueueMapper.setEncounterId(patientQueue.getEncounter().getEncounterId().toString());
                labQueueMapper.setVisitNumber(patientQueue.getVisitNumber());
                if (patientQueue.getEncounter() != null) {
                    labQueueMapper.setOrderMapper(Context.getService(UgandaEMRService.class).processOrders(patientQueue.getEncounter().getOrders(), true));
                }

                List<Identifier> identifiers = new ArrayList<>();
                for (PatientIdentifier patientIdentifier : patientQueue.getPatient().getIdentifiers()) {
                    Identifier identifier = new Identifier(patientIdentifier.getIdentifier(), patientIdentifier.getIdentifierType().getName(), patientIdentifier.getIdentifierType().getUuid());
                    if (patientIdentifier.getLocation() != null) {
                        identifier.setIdentifierLocationUuid(patientIdentifier.getLocation().getUuid());
                    }
                    identifiers.add(identifier);
                }
                labQueueMapper.setPatientIdentifier(identifiers);


                patientQueueMappers.add(labQueueMapper);
            }
        }
        return patientQueueMappers;
    }

    /**
     * Convert PatientQueue List to PatientQueueMapping
     *
     * @param patientQueueList
     * @return
     */
    public List<PharmacyMapper> mapPatientQueueToMapperWithDrugOrders(List<PatientQueue> patientQueueList, boolean includeOrders) {
        List<PharmacyMapper> patientQueueMappers = new ArrayList<>();

        for (PatientQueue patientQueue : patientQueueList) {
            String names = patientQueue.getPatient().getFamilyName() + " " + patientQueue.getPatient().getGivenName() + " " + patientQueue.getPatient().getMiddleName();
            PharmacyMapper pharmacyMapper = new PharmacyMapper();
            pharmacyMapper.setId(patientQueue.getId());
            pharmacyMapper.setPatientQueueUuid(patientQueue.getUuid());
            pharmacyMapper.setPatientNames(names.replace("null", ""));
            pharmacyMapper.setPatientId(patientQueue.getPatient().getPatientId());
            pharmacyMapper.setVisitNumber(patientQueue.getVisitNumber());

            if (patientQueue.getLocationFrom() != null) {
                pharmacyMapper.setLocationFrom(patientQueue.getLocationFrom().getName());
            }

            if (patientQueue.getLocationTo() != null) {
                pharmacyMapper.setLocationTo(patientQueue.getLocationTo().getName());
            }

            if (patientQueue.getProvider() != null) {
                pharmacyMapper.setProviderNames(patientQueue.getProvider().getName());
            }

            pharmacyMapper.setStatus(patientQueue.getStatus().name());
            pharmacyMapper.setAge(patientQueue.getPatient().getAge().toString());
            pharmacyMapper.setDateCreated(patientQueue.getDateCreated().toString());

            if (patientQueue.getDateChanged() != null) {
                pharmacyMapper.setDateChanged(patientQueue.getDateChanged().toString());
            }

            Visit visit = getPatientCurrentVisit(patientQueue.getPatient());

            if (visit != null) {
                pharmacyMapper.setVisitId(visit.getVisitId());
            }

            if (patientQueue.getEncounter() != null) {
                pharmacyMapper.setEncounterId(patientQueue.getEncounter().getEncounterId().toString());
                pharmacyMapper.setEncounterUuid(patientQueue.getEncounter().getUuid());
                if (includeOrders) {
                    pharmacyMapper.setDrugOrderMapper(processDrugOrders(patientQueue.getEncounter().getOrders()));
                }
            }

            List<Identifier> identifiers = new ArrayList<>();
            for (PatientIdentifier patientIdentifier : patientQueue.getPatient().getIdentifiers()) {
                Identifier identifier = new Identifier(patientIdentifier.getIdentifier(), patientIdentifier.getIdentifierType().getName(), patientIdentifier.getIdentifierType().getUuid());
                if (patientIdentifier.getLocation() != null) {
                    identifier.setIdentifierLocationUuid(patientIdentifier.getLocation().getUuid());
                }
                identifiers.add(identifier);
            }
            pharmacyMapper.setPatientIdentifier(identifiers);


            patientQueueMappers.add(pharmacyMapper);
        }
        return patientQueueMappers;
    }

    private String getDispesingLocation(DrugOrder drugOrder) {
        List<Obs> obsList = drugOrder.getEncounter().getAllObs().stream().filter(observation -> observation.getValueCoded() != null && observation.getValueCoded().equals(drugOrder.getConcept()) && !observation.getVoided()).map(Obs::getObsGroup).collect(Collectors.toList());
        List<Obs> obsGroupMembers = new ArrayList<>();
        obsList.forEach(childNode -> {
            obsGroupMembers.addAll(childNode.getGroupMembers());
        });

        List<String> locationUUID = obsGroupMembers.stream().filter(obs -> obs.getConcept().getConceptId().equals(DISPENSING_LOCATION_CONCEPT_ID)).map(Obs::getValueText).collect(Collectors.toList());

        if (!locationUUID.isEmpty()) {
            return locationUUID.get(0);
        }
        return null;

    }

    /**
     * Set Attributes for Observation
     *
     * @param obs
     * @param encounter
     */
    private void setObsAttributes(Obs obs, Encounter encounter) {
        obs.setObsDatetime(encounter.getEncounterDatetime());
        obs.setPerson(encounter.getPatient());
        obs.setLocation(encounter.getLocation());
        obs.setEncounter(encounter);
    }

    /**
     * Get Existing Observation of The Encounter Which the results are going to be returned
     *
     * @param encounter
     * @param concept
     * @param groupingConcept
     * @return
     */
    private Obs getObs(Encounter encounter, Concept concept, Concept groupingConcept) {
        for (Obs obs : encounter.getAllObs()) {
            if (groupingConcept != null) {
                Obs obsGroup = getObs(encounter, groupingConcept, null);
                if (obsGroup.getGroupMembers() != null) {
                    for (Obs member : obsGroup.getGroupMembers()) {
                        if (member.getConcept().equals(concept)) {
                            return member;
                        }
                    }
                }
            } else if (obs.getConcept().equals(concept)) {
                return obs;
            }
        }
        return new Obs();
    }


    /**
     * @param session
     * @param locationToUUID
     * @param nextQueueStatus
     * @param completePreviousQueue
     */
    public void sendPatientToNextLocation(FormEntrySession session, String locationToUUID, String locationFromUUID, PatientQueue.Status nextQueueStatus, boolean completePreviousQueue) {
        PatientQueue patientQueue = new PatientQueue();
        PatientQueueingService patientQueueingService = Context.getService(PatientQueueingService.class);
        Location locationTo = Context.getLocationService().getLocationByUuid(locationToUUID);
        Location locationFrom = Context.getLocationService().getLocationByUuid(locationFromUUID);
        Provider provider = getProviderFromEncounter(session.getEncounter());


        try {
            if (!patientQueueExists(session.getEncounter(), locationTo, locationFrom, nextQueueStatus)) {
                PatientQueue previousQueue = null;
                if (completePreviousQueue) {
                    previousQueue = completePreviousQueue(session.getPatient(), session.getEncounter().getLocation(), PatientQueue.Status.PENDING);
                }
                patientQueue.setLocationFrom(session.getEncounter().getLocation());
                patientQueue.setPatient(session.getEncounter().getPatient());
                patientQueue.setLocationTo(locationTo);
                patientQueue.setQueueRoom(locationTo);
                patientQueue.setProvider(provider);
                patientQueue.setEncounter(session.getEncounter());
                patientQueue.setStatus(nextQueueStatus);
                patientQueue.setCreator(Context.getUserService().getUsersByPerson(provider.getPerson(), false).get(0));
                patientQueue.setDateCreated(new Date());
                patientQueueingService.assignVisitNumberForToday(patientQueue);
                patientQueueingService.savePatientQue(patientQueue);
            }
        } catch (ParseException e) {
            log.error(e);
        }


    }

    public Encounter processLabTestOrdersFromEncounterObs(FormEntrySession session, boolean completePreviousQueue) {

        if (isRetrospective(session.getEncounter())) {
            return session.getEncounter();
        }

        EncounterService encounterService = Context.getEncounterService();
        Set<Order> orders = new HashSet<>();
        Encounter encounter = session.getEncounter();
        CareSetting careSetting = Context.getOrderService().getCareSettingByName(CARE_SETTING_OPD);
        Concept reasonForNextAppointment = Context.getConceptService().getConceptByUuid(CONCEPT_REASON_FOR_NEXT_APPOINTMENT);
        Set<Obs> obsList = obsList = encounter.getObs().stream().filter(obs -> !obs.getConcept().getDatatype().getName().equals("Boolean") && obs.getValueCoded() != null && ((obs.getValueCoded().getConceptClass().getName().equals("LabSet") || obs.getValueCoded().getConceptClass().getName().equals("Test") || obs.getValueCoded().getConceptClass().getName().equals("Radiology/Imaging Procedure"))) && !obs.getConcept().equals(reasonForNextAppointment)).collect(Collectors.toSet());
        for (Obs obs : obsList) {
            if (!orderExists(obs.getValueCoded(), obs.getEncounter())) {
                TestOrder testOrder = new TestOrder();
                testOrder.setConcept(obs.getValueCoded());
                testOrder.setEncounter(obs.getEncounter());
                testOrder.setOrderer(getProviderFromEncounter(obs.getEncounter()));
                testOrder.setPatient(obs.getEncounter().getPatient());
                testOrder.setUrgency(Order.Urgency.STAT);
                testOrder.setCareSetting(careSetting);
                orders.add(testOrder);
            }
        }

        if (!orders.isEmpty()) {
            encounter.setOrders(orders);
            encounterService.saveEncounter(encounter);

            List<Order> labOrder = session.getEncounter().getOrders().stream().filter(order -> (order.getConcept().getConceptClass().getName().equals("LabSet") || order.getConcept().getConceptClass().getName().equals("Test"))).collect(Collectors.toList());
            List<Order> radiologyOrder = session.getEncounter().getOrders().stream().filter(order -> order.getConcept().getConceptClass().getName().equals("Radiology/Imaging Procedure")).collect(Collectors.toList());
            if (!labOrder.isEmpty()) {
                sendPatientToNextLocation(session, LAB_LOCATION_UUID, encounter.getLocation().getUuid(), PatientQueue.Status.PENDING, completePreviousQueue);
            }

            if (!radiologyOrder.isEmpty()) {
                sendPatientToNextLocation(session, RADIOLOGY_LOCATION_UUID, encounter.getLocation().getUuid(), PatientQueue.Status.PENDING, completePreviousQueue);
            }
        }
        return encounter;
    }

    private boolean orderExists(Concept concept, Encounter encounter) {
        List list = Context.getAdministrationService().executeSQL("select order_id from orders where concept_id=" + concept.getConceptId() + " AND encounter_id=" + encounter.getEncounterId(), true);
        boolean orderExists = false;
        if (!list.isEmpty()) {
            orderExists = true;
        }
        return orderExists;
    }


    public boolean patientQueueExists(Encounter encounter, Location locationTo, Location locationFrom, PatientQueue.Status status) throws ParseException {
        List list = Context.getAdministrationService().executeSQL("select patient_queue_id from patient_queue where encounter_id=" + encounter.getEncounterId() + " AND status='" + status.name() + "' AND location_to=" + locationTo.getLocationId() + " AND location_from=" + locationFrom.getLocationId() + " AND date_created BETWEEN \"" + org.openmrs.module.ugandaemr.utils.DateFormatUtil.dateFormtterString(encounter.getEncounterDatetime(), DAY_START_TIME) + "\" AND \"" + org.openmrs.module.ugandaemr.utils.DateFormatUtil.dateFormtterString(encounter.getEncounterDatetime(), DAY_END_TIME) + "\"", true);
        boolean orderExists = false;
        if (!list.isEmpty()) {
            orderExists = true;
        }
        return orderExists;
    }


    public Encounter processDrugOrdersFromEncounterObs(FormEntrySession session, boolean completePreviousQueue) {

        if (isRetrospective(session.getEncounter())) {
            return session.getEncounter();
        }

        EncounterService encounterService = Context.getEncounterService();
        ConceptService conceptService = Context.getConceptService();
        Set<Order> orders = new HashSet<>();
        Encounter encounter = session.getEncounter();
        CareSetting careSetting = Context.getOrderService().getCareSettingByName(CARE_SETTING_OPD);
        Set<Obs> obsList = encounter.getObs().stream().filter(obs -> !obs.getConcept().getDatatype().getName().equals("Boolean") && obs.getValueCoded() != null && (obs.getValueCoded().getConceptClass().getName().equals("Drug"))).collect(Collectors.toSet());

        OrderService orderService = Context.getOrderService();
        for (Obs obs : obsList) {
            if (!orderExists(obs.getValueCoded(), obs.getEncounter())) {
                DrugOrder drugOrder = new DrugOrder();
                Set<Obs> obsGroupMembers = new HashSet<>();
                if (obs.getObsGroup() != null) {
                    obsGroupMembers.addAll((obs.getObsGroup().getGroupMembers()));

                    for (Obs groupMember : obsGroupMembers) {

                        switch (groupMember.getConcept().getConceptId()) {
                            case MEDICATION_QUANTITY_CONCEPT_ID:
                            case ARV_MEDICATION_QUANTITY_CONCEPT_ID:
                                drugOrder.setQuantity(groupMember.getValueNumeric());
                                drugOrder.setDose(groupMember.getValueNumeric());
                                break;
                            case MEDICATION_DURATION_CONCEPT_ID:
                            case ARV_MEDICATION_DURATION_CONCEPT_ID:
                                drugOrder.setDuration(groupMember.getValueNumeric().intValue());
                                break;
                            case MEDICATION_QUANTITY_UNIT_CONCEPT_ID:
                                drugOrder.setQuantityUnits(groupMember.getValueCoded());
                                break;
                            case MEDICATION_DURATION_UNIT_CONCEPT_ID:
                                drugOrder.setDurationUnits(groupMember.getValueCoded());
                                break;
                            case MEDICATION_COMMENT_CONCEPT_ID:
                                drugOrder.setCommentToFulfiller(groupMember.getValueText());
                                break;
                            case DRUG_ID_CONCEPT_ID:
                                drugOrder.setDrug(Context.getConceptService().getDrugByUuid(groupMember.getValueText()));
                                break;
                            case MEDICATION_FREQUENCY:
                                drugOrder.setFrequency(Context.getOrderService().getOrderFrequencyByConcept(groupMember.getValueCoded()));
                                break;
                            case MEDICATION_DOSE_INSTRUCTION:
                                drugOrder.setDosingInstructions(groupMember.getValueText());
                                break;
                            default:
                        }
                    }

                    if (drugOrder.getDose() == null) {
                        drugOrder.setDose(0.0);
                    }

                    if (drugOrder.getDoseUnits() == null) {
                        drugOrder.setDoseUnits(conceptService.getConcept(DEFALUT_DOSE_UNIT_CONCEPT_ID));
                    }

                    if (drugOrder.getRoute() == null) {
                        drugOrder.setRoute(conceptService.getConcept(DEFALUT_ROUTE_CONCEPT_ID));
                    }

                    if (drugOrder.getDurationUnits() == null) {
                        drugOrder.setDurationUnits(conceptService.getConcept(DEFALUT_DURATION_UNIT_CONCEPT_ID));
                    }

                    if (drugOrder.getFrequency() == null) {
                        drugOrder.setFrequency(Context.getOrderService().getOrderFrequencyByUuid(DEFALUT_ORDER_FREQUECNY_UUID));
                    }

                    if (drugOrder.getQuantityUnits() == null) {
                        drugOrder.setQuantityUnits(conceptService.getConcept(DEFALUT_DISPENSING_UNIT_CONCEPT_ID));
                    }

                    drugOrder.setNumRefills(1);
                    drugOrder.setEncounter(obs.getEncounter());
                    drugOrder.setOrderer(getProviderFromEncounter(obs.getEncounter()));
                    drugOrder.setPatient(obs.getEncounter().getPatient());
                    drugOrder.setUrgency(Order.Urgency.STAT);
                    drugOrder.setCareSetting(careSetting);
                    drugOrder.setConcept(obs.getValueCoded());
                    discontinueOverLappingDrugOrders(drugOrder);

                    if (isValidDrugOrder(drugOrder)) {
                        orders.add(drugOrder);
                    }

                }
            }
        }
        if (!orders.isEmpty()) {
            encounter.setOrders(orders);
            encounterService.saveEncounter(encounter);
            List<String> orderLocations = new ArrayList<>();
            if (!session.getEncounter().getOrders().isEmpty()) {
                boolean enableStockManagement = Boolean.parseBoolean(Context.getAdministrationService().getGlobalProperty("ugandaemr.enableStockManagement"));
                if (enableStockManagement) {
                    orders.forEach(order -> {
                        orderLocations.add(getDispesingLocation((DrugOrder) order));
                    });

                    orderLocations.forEach(orderLocation -> {
                        sendPatientToNextLocation(session, orderLocation, encounter.getLocation().getUuid(), PatientQueue.Status.PENDING, completePreviousQueue);
                    });
                } else {
                    sendPatientToNextLocation(session, PHARMACY_LOCATION_UUID, encounter.getLocation().getUuid(), PatientQueue.Status.PENDING, completePreviousQueue);
                }


                completePreviousQueue(session.getPatient(), session.getEncounter().getLocation(), PatientQueue.Status.PENDING);
            }
        }
        return encounter;
    }

    /**
     * This Validates a drug order
     *
     * @param drugOrder the drug order to validate
     * @return returns true or false basing on the validation
     */
    private boolean isValidDrugOrder(DrugOrder drugOrder) {
        if (drugOrder.getDuration() == null || drugOrder.getQuantity() == null || drugOrder.getFrequency() == null || drugOrder.getQuantityUnits() == null || drugOrder.getRoute() == null || drugOrder.getDose() == null || drugOrder.getDoseUnits() == null) {
            return false;
        } else {
            return true;
        }
    }

    public Provider getProviderFromEncounter(Encounter encounter) {
        EncounterRole encounterRole = Context.getEncounterService().getEncounterRoleByUuid(ENCOUNTER_ROLE);

        Set<Provider> providers = encounter.getProvidersByRole(encounterRole);
        List<Provider> providerList = new ArrayList<>();
        for (Provider provider : providers) {
            providerList.add(provider);
        }

        if (!providerList.isEmpty()) {
            return providerList.get(0);
        } else {
            return null;
        }
    }

    public PatientQueue completePreviousQueue(Patient patient, Location location, PatientQueue.Status searchStatus) {
        PatientQueueingService patientQueueingService = Context.getService(PatientQueueingService.class);
        PatientQueue patientQueue = getPreviousQueue(patient, location, searchStatus);
        if (patientQueue != null) {
            patientQueueingService.completePatientQueue(patientQueue);
        }
        return patientQueue;
    }

    public PatientQueue getPreviousQueue(Patient patient, Location location, PatientQueue.Status status) {
        PatientQueueingService patientQueueingService = Context.getService(PatientQueueingService.class);
        PatientQueue previousQueue = null;

        List<PatientQueue> patientQueueList = patientQueueingService.getPatientQueueList(null, OpenmrsUtil.firstSecondOfDay(new Date()), OpenmrsUtil.getLastMomentOfDay(new Date()), location, null, patient, null);

        if (!patientQueueList.isEmpty()) {
            previousQueue = patientQueueList.get(0);
        }
        return previousQueue;
    }


    private boolean isRetrospective(Encounter encounter) {
        return encounter.getEncounterDatetime().before(OpenmrsUtil.firstSecondOfDay(new Date()));
    }


    /**
     * This Method gets the latest current visit for a patient
     *
     * @param patient the patient whose current visit will be retrived.
     * @return Visit the active visit for a patient.
     */
    private Visit getPatientCurrentVisit(Patient patient) {
        List<Visit> visitList = Context.getVisitService().getActiveVisitsByPatient(patient);
        for (Visit visit : visitList) {
            if (visit.getStartDatetime().after(OpenmrsUtil.firstSecondOfDay(new Date())) && visit.getStartDatetime().before(OpenmrsUtil.getLastMomentOfDay(new Date()))) {
                return visit;
            }
        }
        return null;
    }

    public void completePatientActiveVisit(Patient patient) {
        VisitService visitService = Context.getVisitService();
        List<Visit> activeVisitsByPatient = visitService.getActiveVisitsByPatient(patient);
        for (Visit visit : activeVisitsByPatient) {
            if (visit.getVisitType().equals(visitService.getVisitTypeByUuid("7b0f5697-27e3-40c4-8bae-f4049abfb4ed"))) {
                try {
                    visitService.endVisit(visit, OpenmrsUtil.getLastMomentOfDay(visit.getStartDatetime()));
                } catch (Exception e) {
                    log.error("Competition of Patient Visit #" + visit.getVisitId() + " failed.", e);
                }
            }
        }
    }

    /**
     * @see org.openmrs.module.ugandaemr.api.UgandaEMRService#dispenseMedication(org.openmrs.module.ugandaemr.pharmacy.DispensingModelWrapper, org.openmrs.Provider, org.openmrs.Location)
     */
    public SimpleObject dispenseMedication(DispensingModelWrapper resultWrapper, Provider provider, Location location) {
        boolean enableStockManagement = Boolean.parseBoolean(Context.getAdministrationService().getGlobalProperty("ugandaemr.enableStockManagement"));
        List<Boolean> completePatientQueue = new ArrayList<>();
        if (enableStockManagement) {
            SimpleObject simpleObject = validateStock(resultWrapper);
            if (simpleObject.get("errors") != null) {
                return simpleObject;
            }
        }
        EncounterService encounterService = Context.getEncounterService();
        PatientQueueingService patientQueueingService = Context.getService(PatientQueueingService.class);

        Encounter previousEncounter = encounterService.getEncounter(resultWrapper.getEncounterId());
        PatientQueue patientQueue = patientQueueingService.getPatientQueueById(resultWrapper.getPatientQueueId());
        Encounter encounter = getDispensingEncounter(previousEncounter, location, provider);
        List<DrugOrderMapper> referredOutPrescriptions = new ArrayList<>();
        Set<Obs> obs = new HashSet<>();
        for (DrugOrderMapper drugOrderMapper : resultWrapper.getDrugOrderMappers()) {
            DrugOrder drugOrder = (DrugOrder) Context.getOrderService().getOrder(drugOrderMapper.getOrderId());

            if (drugOrderMapper.getOrderReasonNonCoded() != null && drugOrderMapper.getOrderReasonNonCoded().equals("REFERREDOUT")) {
                try {
                    obs.addAll(processDispensingObservation(encounter, drugOrderMapper, false));
                } catch (ParseException e) {
                    log.error(e);
                }
                drugOrderMapper.setQuantity(calculatePrescriptionDispenseDifference(drugOrderMapper, drugOrder));
                drugOrderMapper.setPatientAge(drugOrder.getPatient().getAge());
                referredOutPrescriptions.add(drugOrderMapper);
            } else {
                try {
                    obs.addAll(processDispensingObservation(encounter, drugOrderMapper, true));
                } catch (ParseException e) {
                    log.error(e);
                }
            }

            try {
                if (!drugOrderMapper.getKeepOrder()) {
                    Context.getOrderService().discontinueOrder(drugOrder, "Completed", new Date(), provider, previousEncounter);
                } else {
                    completePatientQueue.add(false);
                }
            } catch (Exception e) {
                log.error(e);
            }

            Context.getService(UgandaEMRService.class).completePatientActiveVisit(patientQueue.getPatient());
        }

        encounter.setObs(obs);


        try {
            if (enableStockManagement) {
                reduceStockBalances(resultWrapper);
            }

            encounterService.saveEncounter(encounter);
            if (!completePatientQueue.contains(false)) {
                patientQueue.setEncounter(encounter);
                patientQueueingService.savePatientQue(patientQueue);
                patientQueueingService.completePatientQueue(patientQueue);
            }
        } catch (Exception e) {
            log.error(e);
        }

        ObjectMapper objectMapper = new ObjectMapper();

        SimpleObject simpleObject = new SimpleObject();

        if (!referredOutPrescriptions.isEmpty()) {
            try {
                simpleObject.put("referredOutPrescriptions", objectMapper.writeValueAsString(referredOutPrescriptions));
            } catch (IOException e) {
                log.error(e);
            }
        } else {
            simpleObject = SimpleObject.create("status", "success", "message", "Saved!");
        }
        return simpleObject;
    }

    private Encounter getDispensingEncounter(Encounter previousEncounter, Location location, Provider provider) {
        EncounterService encounterService = Context.getEncounterService();
        Encounter encounter = null;
        Collection<Visit> visits = new ArrayList<>();
        visits.add(previousEncounter.getVisit());
        Collection<EncounterType> encounterTypes = new ArrayList<>();
        encounterTypes.add(Context.getEncounterService().getEncounterTypeByUuid(ENCOUNTER_TYPE_DISPENSE_UUID));
        EncounterSearchCriteria encounterSearchCriteria = new EncounterSearchCriteriaBuilder().setEncounterTypes(encounterTypes).setVisits(visits).setFromDate(OpenmrsUtil.firstSecondOfDay(previousEncounter.getVisit().getStartDatetime())).setToDate(OpenmrsUtil.getLastMomentOfDay(previousEncounter.getVisit().getStartDatetime())).createEncounterSearchCriteria();

        List<Encounter> dispensingEncounter = Context.getEncounterService().getEncounters(encounterSearchCriteria);

        if (dispensingEncounter.size() > 0) {
            encounter = dispensingEncounter.get(0);
        } else {
            encounter = new Encounter();
            encounter.setEncounterType(encounterService.getEncounterTypeByUuid(ENCOUNTER_TYPE_DISPENSE_UUID));
            encounter.setProvider(Context.getEncounterService().getEncounterRoleByUuid(ENCOUNTER_ROLE_PHARMACIST), provider);
            encounter.setLocation(location);
            encounter.setPatient(previousEncounter.getPatient());
            encounter.setVisit(previousEncounter.getVisit());
            encounter.setEncounterDatetime(previousEncounter.getEncounterDatetime());
            encounter.setForm(Context.getFormService().getFormByUuid(DISPENSE_FORM_UUID));
        }
        return encounter;
    }

    private SimpleObject validateStock(DispensingModelWrapper dispensingModelWrapper) {
        SimpleObject simpleObject = SimpleObject.create("status", "failed", "message", "failed", "errors", null);
        new SimpleObject();
        ObjectMapper objectMapper = new ObjectMapper();
        List<Object> errors = new ArrayList<>();

        dispensingModelWrapper.getDrugOrderMappers().forEach(drugOrderMapper -> {
            if (drugOrderMapper.getQuantity() != null && drugOrderMapper.getQuantity() > 0 && (drugOrderMapper.getMaxDispenseValue() == null || drugOrderMapper.getMaxDispenseValue().equals(""))) {
                errors.add("Max Dispensing Value is null for Drug " + drugOrderMapper.getConceptName());
            } else if (drugOrderMapper.getMaxDispenseValue() != null && drugOrderMapper.getQuantity() != null && drugOrderMapper.getMaxDispenseValue() < drugOrderMapper.getQuantity()) {
                errors.add("The quantity dispensed is greater than max dispensing Value: " + drugOrderMapper.getMaxDispenseValue() + " for drug " + drugOrderMapper.getConceptName());
            } else if (drugOrderMapper.getQuantity() != null && drugOrderMapper.getQuantity() < 0) {
                errors.add("Negative Dispensing Value is not allowed for Drug " + drugOrderMapper.getConceptName());
            }

            if (drugOrderMapper.getQuantity() != null && drugOrderMapper.getQuantity() > 0 && StringUtils.isBlank(drugOrderMapper.getStockBatchNo())) {
                errors.add("BatchNo is Empty for Drug " + drugOrderMapper.getConceptName());
            }
        });
        try {
            if (errors.size() > 0) {
                simpleObject.put("errors", objectMapper.writeValueAsString(errors));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return simpleObject;
    }

    private void reduceStockBalances(DispensingModelWrapper resultWrapper) {
        List<DispenseRequest> dispenseRequests = new ArrayList<>();
        resultWrapper.getDrugOrderMappers().forEach(drugOrderMapper1 -> {
            if (StringUtils.isNotBlank(drugOrderMapper1.getStockBatchNo()) && drugOrderMapper1.getQuantity() != null && drugOrderMapper1.getQuantity() > 0) {
                DispenseRequest dispenseRequest = new DispenseRequest();
                dispenseRequest.setEncounterId(drugOrderMapper1.getEncounterId());
                dispenseRequest.setOrderId(drugOrderMapper1.getOrderId());
                dispenseRequest.setLocationUuid(drugOrderMapper1.getDispensingLocation());
                dispenseRequest.setPatientId(drugOrderMapper1.getPatientId());
                dispenseRequest.setStockItemUuid(drugOrderMapper1.getStockItem());
                dispenseRequest.setStockBatchUuid(drugOrderMapper1.getStockBatchNo());
                dispenseRequest.setQuantity(BigDecimal.valueOf(drugOrderMapper1.getQuantity()));
                dispenseRequest.setStockItemPackagingUOMUuid(drugOrderMapper1.getStockQuantityUnitUuid());
                dispenseRequests.add(dispenseRequest);
            }
        });
        if (dispenseRequests.size() > 0) {
            Context.getService(StockManagementService.class).dispenseStockItems(dispenseRequests);
        }
    }

    /**
     * This Method processes dispensing observations
     *
     * @param encounter          encounter where the obs will be saved
     * @param drugOrderMapper    the data for the drugs that are being dispensed
     * @param receivedAtFacility boolean to check if the drugs were dispensed at facility or not
     * @return a set of drug dispensing observations
     */
    private Set<Obs> processDispensingObservation(Encounter encounter, DrugOrderMapper drugOrderMapper, Boolean receivedAtFacility) throws ParseException {

        ConceptService conceptService = Context.getConceptService();
        Set<Obs> obs = new HashSet<>();
        Order order = null;
        if (drugOrderMapper.getOrderId() != null) {
            order = Context.getOrderService().getOrder(drugOrderMapper.getOrderId());
        }
        //Grouping Observation
        Obs parentObs = createDispensingObs(encounter, conceptService.getConcept(MEDICATION_DISPENSE_SET), null, null, order);
        obs.add(parentObs);

        //Drug Observation
        if (drugOrderMapper.getConcept() != null) {
            Obs drug = createDispensingObs(encounter, conceptService.getConcept(MEDICATION_ORDER_CONCEPT_ID), drugOrderMapper.getConcept(), "coded", order);
            parentObs.addGroupMember(drug);
            obs.add(drug);
        }

        //Quantity Observation
        if (drugOrderMapper.getQuantity() != null) {
            Obs drugQuantity = createDispensingObs(encounter, conceptService.getConcept(MEDICATION_DISPENSE_QUANTITY), drugOrderMapper.getQuantity().toString(), "numeric", order);
            parentObs.addGroupMember(drugQuantity);
            obs.add(drugQuantity);
        }

        //Duration Observation
        if (drugOrderMapper.getDuration() != null) {
            Obs periodDispensed = createDispensingObs(encounter, conceptService.getConcept(MEDICATION_DURATION_CONCEPT_ID), drugOrderMapper.getDuration().toString(), "numeric", order);
            parentObs.addGroupMember(periodDispensed);
            obs.add(periodDispensed);
        }


        //Duration Observation
        if (!drugOrderMapper.getStrength().equals("")) {
            Obs drugStrength = createDispensingObs(encounter, conceptService.getConcept(MEDICATION_STRENGTH_CONCEPT_ID), drugOrderMapper.getStrength(), "string", order);
            parentObs.addGroupMember(drugStrength);
            obs.add(drugStrength);
        }

        //check if issued at facility

        Obs dispensedAtFacility = createDispensingObs(encounter, conceptService.getConcept(MEDICATION_DISPENSE_RECEIVED_AT_VIST), null, null, order);
        if (receivedAtFacility) {
            dispensedAtFacility.setValueCoded(conceptService.getConcept(MEDICATION_DISPENSE_RECEIVED_AT_VIST_YES));
        } else {
            dispensedAtFacility.setValueCoded(conceptService.getConcept(MEDICATION_DISPENSE_RECEIVED_AT_VIST_NO));
        }
        parentObs.addGroupMember(dispensedAtFacility);
        obs.add(dispensedAtFacility);

        return obs;
    }

    /**
     * This method helps create an observation
     *
     * @param encounter observation encounter
     * @param concept   question for observation
     * @param value     value for the observation
     * @param valueType datatype for the observation
     * @param order     observation order
     * @return an observation
     * @throws ParseException
     */
    private Obs createDispensingObs(Encounter encounter, Concept concept, String value, String valueType, Order order) throws ParseException {
        Obs obs = new Obs();
        obs.setObsDatetime(encounter.getEncounterDatetime());
        obs.setPerson(encounter.getPatient());
        obs.setLocation(encounter.getLocation());
        obs.setEncounter(encounter);
        obs.setOrder(order);
        obs.setConcept(concept);
        if (valueType != null) {
            if (valueType.equals("string")) {
                obs.setValueAsString(value);
            } else if (valueType.equals("numeric")) {
                obs.setValueNumeric(Double.parseDouble(value));
            } else if (valueType.equals("coded")) {
                obs.setValueCoded(Context.getConceptService().getConcept(value));
            } else if (valueType.equals("groupId")) {
                obs.setValueGroupId(Integer.parseInt(value));
            }
        }
        return obs;
    }

    /**
     * Calculates the balance after dispensing medication to patient.
     *
     * @param drugOrderMapper the object that contains the data of dispensing
     * @param drugOrder       the object that contains prescription data
     * @return
     */
    private Double calculatePrescriptionDispenseDifference(DrugOrderMapper drugOrderMapper, DrugOrder drugOrder) {
        Double quantityBalance = 0.0;
        if (drugOrderMapper.getQuantity() != null && drugOrder.getQuantity() != null) {
            quantityBalance = drugOrder.getQuantity() - drugOrderMapper.getQuantity();
        } else if (drugOrder.getQuantity() != null && drugOrderMapper.getQuantity() == null) {
            quantityBalance = drugOrder.getQuantity();
        }
        return quantityBalance;
    }

    /**
     * Check if there is a similar active drug order and discontinues it.
     *
     * @param order the order to be checked if it is similar to any
     */
    private void discontinueOverLappingDrugOrders(Order order) {
        OrderService orderService = Context.getOrderService();
        List<Order> activeOrders = orderService.getActiveOrders(order.getPatient(), null, order.getCareSetting(), new Date());
        for (Order activeOrder : activeOrders) {
            if (order.hasSameOrderableAs(activeOrder)
                    && !OpenmrsUtil.nullSafeEquals(order.getPreviousOrder(), activeOrder)
                    && OrderUtil.checkScheduleOverlap(order, activeOrder) && activeOrder.getOrderType()
                    .equals(Context.getOrderService().getOrderTypeByUuid(OrderType.DRUG_ORDER_TYPE_UUID))) {
                try {
                    orderService.discontinueOrder(activeOrder, "Incomplete with new similar order", OpenmrsUtil.getLastMomentOfDay(activeOrder.getDateActivated()), order.getOrderer(), activeOrder.getEncounter());
                } catch (Exception e) {
                    log.error("failed to discontinue order #" + activeOrder.getOrderId(), e);
                }
            }
        }
    }

    /**
     * @see org.openmrs.module.ugandaemr.api.UgandaEMRService#generatePatientProgramAttribute(org.openmrs.ProgramAttributeType, org.openmrs.PatientProgram, java.lang.String)
     */
    public PatientProgramAttribute generatePatientProgramAttribute(ProgramAttributeType programAttributeType, PatientProgram patientProgram, String value) {
        PatientProgramAttribute patientProgramAttribute = new PatientProgramAttribute();
        patientProgramAttribute.setAttributeType(programAttributeType);
        patientProgramAttribute.setValueReferenceInternal(value);

        return patientProgramAttribute;
    }

    /**
     * @see org.openmrs.module.ugandaemr.api.UgandaEMRService#generatePatientProgramAttributeFromObservation(org.openmrs.PatientProgram, java.util.Set, java.lang.Integer, java.lang.String)
     */
    public PatientProgramAttribute generatePatientProgramAttributeFromObservation(PatientProgram patientProgram, Set<Obs> observations, Integer conceptID, String programAttributeUUID) {
        UgandaEMRService ugandaEMRService = Context.getService(UgandaEMRService.class);
        for (Obs obs : observations) {
            if (conceptID.equals(obs.getConcept().getConceptId())) {
                ProgramAttributeType programAttributeType = Context.getProgramWorkflowService().getProgramAttributeTypeByUuid(programAttributeUUID);
                return ugandaEMRService.generatePatientProgramAttribute(programAttributeType, patientProgram, obs.getValueAsString(Locale.ENGLISH));
            }
        }
        return null;
    }


    public Encounter processRetrospectiveViralLoadOrder(Obs viralLoadRequestObservation, Obs accessionNumber, Obs specimenSource) {

        EncounterService encounterService = Context.getEncounterService();
        Set<Order> orders = new HashSet<>();
        CareSetting careSetting = Context.getOrderService().getCareSettingByName(CARE_SETTING_OPD);
        Encounter encounter = viralLoadRequestObservation.getEncounter();

        TestOrder testOrder = new TestOrder();

        testOrder.setConcept(viralLoadRequestObservation.getValueCoded());
        testOrder.setEncounter(viralLoadRequestObservation.getEncounter());
        testOrder.setOrderer(getProviderFromEncounter(viralLoadRequestObservation.getEncounter()));
        testOrder.setAccessionNumber(accessionNumber.getValueText());
        testOrder.setPatient(viralLoadRequestObservation.getEncounter().getPatient());
        testOrder.setUrgency(Order.Urgency.STAT);
        testOrder.setCareSetting(careSetting);
        testOrder.setOrderType(Context.getOrderService().getOrderTypeByUuid(TEST_ORDER_TYPE_UUID));
        testOrder.setAction(Order.Action.NEW);
        testOrder.setInstructions("REFER TO " + "cphl");
        testOrder.setSpecimenSource(specimenSource.getValueCoded());
        orders.add(testOrder);

        encounter.setOrders(orders);
        encounterService.saveEncounter(encounter);

        return encounter;
    }

    /**
     * @see org.openmrs.module.ugandaemr.api.UgandaEMRService#saveOrderObs(org.openmrs.module.ugandaemr.api.lab.OrderObs)
     */
    public OrderObs saveOrderObs(OrderObs orderObs) {
        OrderObs existingOrderObs = getOrderObsByObs(orderObs.getObs());
        if (existingOrderObs == null) {
            return dao.saveOrderObs(orderObs);
        } else {
            orderObs = existingOrderObs;
            return orderObs;
        }
    }

    /**
     * @see org.openmrs.module.ugandaemr.api.UgandaEMRService#getOrderObs(org.openmrs.Encounter, java.util.Date, java.util.Date, java.util.List, java.util.List, boolean)
     */
    public List<OrderObs> getOrderObs(Encounter encounter, Date onOrBefore, Date onOrAfter, List<Order> orders, List<Obs> obs, boolean includeVoided) {
        return dao.getOrderObs(encounter, onOrBefore, onOrAfter, orders, obs, includeVoided);
    }

    /**
     * @see org.openmrs.module.ugandaemr.api.UgandaEMRService#getOrderObsByObs(org.openmrs.Obs)
     */
    @Override
    public OrderObs getOrderObsByObs(Obs obs) {
        return dao.getOrderObsByObs(obs);
    }

    /**
     * @see org.openmrs.module.ugandaemr.api.UgandaEMRService#getAllOrderObs()
     */
    public List<OrderObs> getAllOrderObs() {
        return getOrderObs(null, null, null, null, null, false);
    }

    /**
     * @see org.openmrs.module.ugandaemr.api.UgandaEMRService#getOrderObsById(java.lang.Integer)
     */
    public OrderObs getOrderObsById(Integer orderObsId) {
        return dao.getOrderObsById(orderObsId);
    }

    /**
     * @see org.openmrs.module.ugandaemr.api.UgandaEMRService#getOrderObsByUuid(java.lang.String)
     */
    @Override
    public OrderObs getOrderObsByUuid(String uuid) {
        return dao.getOrderObsByUuid(uuid);
    }

    @Override
    public OrderObs getOrderObsByOrder(Order order) {
        return dao.getOrderObsByOrder(order);
    }


    public PatientQueue sendPatientBackToClinician(Encounter encounter, Location locationTo, Location locationFrom, String previousQueueStatus) {
        PatientQueue patientQueue = new PatientQueue();

        PatientQueueingService patientQueueingService = Context.getService(PatientQueueingService.class);
        UgandaEMRService ugandaEMRService = Context.getService(UgandaEMRService.class);
        Provider provider = ugandaEMRService.getProviderFromEncounter(encounter);

        SimpleObject simpleObject = new SimpleObject();
        SimpleObject orders = null;
        try {
            simpleObject = ugandaEMRService.getProcessedOrders(PROCESSED_ORDER_WITHOUT_RESULT_QUERY.concat(" AND patient_id=" + encounter.getPatient().getPatientId()), encounter.getDateCreated(), false);
            orders = (SimpleObject) simpleObject.get("ordersList");
        } catch (ParseException | IOException e) {
            log.error(e);
        }

        if (orders == null) {
            ugandaEMRService.completePreviousQueue(encounter.getPatient(), encounter.getLocation(), PatientQueue.Status.PENDING);
        }

        List<PatientQueue> patientQueueList = patientQueueingService.getPatientQueueList(null, OpenmrsUtil.firstSecondOfDay(new Date()), OpenmrsUtil.getLastMomentOfDay(new Date()), null, null, encounter.getPatient(), null);

        List<PatientQueue> fromLabQueue = new ArrayList<>();


        for (PatientQueue potentialQueueFromLab : patientQueueList) {
            Encounter labEncounter = potentialQueueFromLab.getEncounter();
            PatientQueue.Status labStatus = potentialQueueFromLab.getStatus();
            boolean queueStatus = labStatus.equals(PatientQueue.Status.PENDING) || labStatus.equals(PatientQueue.Status.PICKED);
            if (labEncounter != null && labEncounter.equals(encounter) && queueStatus && potentialQueueFromLab.getLocationFrom() == locationFrom && potentialQueueFromLab.getLocationTo().equals(encounter.getLocation())) {
                fromLabQueue.add(patientQueue);
            }
        }

        boolean queueExists = false;
        try {
            queueExists = patientQueueExists(encounter, encounter.getLocation(), locationFrom, PatientQueue.Status.PENDING);
        } catch (ParseException e) {
            log.error(e);
        }

        if (!queueExists) {
            if (fromLabQueue.isEmpty()) {
                patientQueue.setLocationFrom(locationFrom);
                patientQueue.setPatient(encounter.getPatient());
                patientQueue.setLocationTo(encounter.getLocation());
                patientQueue.setQueueRoom(encounter.getLocation());
                patientQueue.setProvider(provider);
                patientQueue.setEncounter(encounter);
                patientQueue.setStatus(PatientQueue.Status.PENDING);
                patientQueue.setCreator(Context.getUserService().getUsersByPerson(provider.getPerson(), false).get(0));
                patientQueue.setDateCreated(new Date());
                patientQueueingService.assignVisitNumberForToday(patientQueue);
                patientQueueingService.savePatientQue(patientQueue);
            }
        }

        return patientQueue;
    }

    public String generateLabNumber(String orderUuid) {
        Order order = Context.getOrderService().getOrderByUuid(orderUuid);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String date = sdf.format(new Date());
        String defaultSampleId = ("LAB" + "-" + order.getPatient().getPatientId() + "-" + date).replace("/", "-");
        return defaultSampleId;
    }

    /**
     * @see org.openmrs.module.ugandaemr.api.UgandaEMRService#accessionLabTest(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public TestOrder accessionLabTest(String orderUuid, String accessionNumber, String specimenSourceUuid, String instructions) {
        OrderService orderService = Context.getOrderService();
        Order order = orderService.getOrderByUuid(orderUuid);
        TestOrder testOrder = null;
        if (!instructions.equals("")) {
            testOrder = new TestOrder();
            testOrder.setAccessionNumber(accessionNumber);
            testOrder.setInstructions("REFER TO " + instructions.toUpperCase());
            testOrder.setConcept(order.getConcept());
            testOrder.setEncounter(order.getEncounter());
            testOrder.setOrderer(order.getOrderer());
            testOrder.setPatient(order.getPatient());
            testOrder.setUrgency(Order.Urgency.STAT);
            testOrder.setCareSetting(order.getCareSetting());
            testOrder.setOrderType(order.getOrderType());
            testOrder.setPreviousOrder(order);
            testOrder.setAction(Order.Action.REVISE);
            testOrder.setFulfillerStatus(Order.FulfillerStatus.IN_PROGRESS);
            testOrder.setSpecimenSource(Context.getConceptService().getConceptByUuid(specimenSourceUuid));
            orderService.saveOrder(testOrder, null);
            orderService.updateOrderFulfillerStatus(order, Order.FulfillerStatus.IN_PROGRESS, "Order Sent to CPHHL");
            orderService.voidOrder(order, "REVISED with new order " + testOrder.getOrderNumber());
        } else {
            testOrder = (TestOrder) orderService.updateOrderFulfillerStatus(order, Order.FulfillerStatus.IN_PROGRESS, "To be processed", accessionNumber);
            updateSpecimenSourceManually(order, specimenSourceUuid);
        }
        return testOrder;
    }

    private void updateSpecimenSourceManually(Order order, String specimenSourceUUID) {
        Concept specimenSource = Context.getConceptService().getConceptByUuid(specimenSourceUUID);
        if (specimenSource != null) {
            Context.getAdministrationService().executeSQL(String.format(SPECIMEN_MANUAL_UPDATE_QUERY, specimenSource.getConceptId(), order.getOrderId()), false);
        }
    }


    public Map initializeMetaData(){

        Map results = new HashMap<>();
        AdministrationService administrationService = Context.getAdministrationService();

        try {
            log.info("Start import of Concepts,privillages,personAttribute provider attribute type etc...");
            importMetaDataFromXMLFiles();
            log.info("completed import of Concepts,privillages,personAttribute provider attribute type etc...");
            // run the initialization of forms
            for (Initializer initializer : initialiseForms()) {
                initializer.started();
            }

            results.put("status", "success");
            results.put("message", "completed initialising metadata");

            return results;

        } catch (Exception e) {
            results.put("status", "failed");
            results.put("message", e.getMessage());
            return results;
        }
    }

    private String getMetadataPath(String type) {
        String appDataDir = "";


        // Check if metadata initialization is enabled
        boolean initialiseMetadataOnStart = Boolean.parseBoolean(
                Context.getAdministrationService().getGlobalProperty("ugandaemr.initialiseMetadataOnStart")
        );

        if (initialiseMetadataOnStart && !type.equals("metadata")) {
            final ResourceFactory resourceFactory = ResourceFactory.getInstance();
            final ResourceProvider resourceProvider = resourceFactory.getResourceProviders().get(MODULE_ID);
            appDataDir = resourceProvider.getResource(type).getPath();
        } else {
            appDataDir = OpenmrsUtil.getApplicationDataDirectory();
        }

        // Default paths when initializing metadata on start
        Map<String, String> defaultPaths = new HashMap<>();
        defaultPaths.put("jsonforms", "jsonforms/");
        defaultPaths.put("htmlforms", "htmlforms/");
        defaultPaths.put("metadata", "metadata/");

        String relativePath;

        if (initialiseMetadataOnStart) {
            // Get path from default paths
            relativePath = defaultPaths.get(type);
        } else {
            // Get path from global properties
            switch (type) {
                case "jsonforms":
                    relativePath = Context.getAdministrationService().getGlobalProperty("ugandaemr.metadata.jsonFormPath");
                    break;
                case "htmlforms":
                    relativePath = Context.getAdministrationService().getGlobalProperty("ugandaemr.metadata.htmlFormPath");
                    break;
                case "metadata":
                    relativePath = Context.getAdministrationService().getGlobalProperty("ugandaemr.metadata.path");
                    break;
                case "configuration":
                    relativePath = Context.getAdministrationService().getGlobalProperty("ugandaemr.configuration");
                    break;
                case "frontend":
                    relativePath = Context.getAdministrationService().getGlobalProperty("ugandaemr.frontend");
                    break;
                case "modules":
                    relativePath = Context.getAdministrationService().getGlobalProperty("ugandaemr.modules");
                    break;
                default:
                    relativePath = null;
            }
        }

        // Validate path
        if (relativePath == null || relativePath.isEmpty()) {
            throw new IllegalArgumentException("No valid path found for type: " + type);
        }

        // Return the appropriate path
        return Paths.get(appDataDir, relativePath).toString();
    }

    public void importMetaDataFromXMLFiles(){
        DataImporter dataImporter = Context.getRegisteredComponent("dataImporter", DataImporter.class);
        String metaDataFilePath=getMetadataPath("metadata")+"/";
        log.info("import  to Concept Table  Starting");
        dataImporter.importData(metaDataFilePath+"concepts_and_drugs/Concept.xml");
        log.info("import to Concept Table  Successful");

        log.info("import  to Concept Name Table  Starting");
        dataImporter.importData(metaDataFilePath+"concepts_and_drugs/Concept_Name.xml");
        log.info("import to Concept Name Table  Successful");

        log.info("import  to Concept_Description Table  Starting");
        dataImporter.importData(metaDataFilePath+"concepts_and_drugs/Concept_Description.xml");
        log.info("import to Concept_Description Table  Successful");

        log.info("import  to Concept_Numeric Table  Starting");
        dataImporter.importData(metaDataFilePath+"concepts_and_drugs/Concept_Numeric.xml");
        log.info("import to Concept_Numeric Table  Successful");

        log.info("import  to Concept_Answer Table  Starting");
        dataImporter.importData(metaDataFilePath+"concepts_and_drugs/Concept_Answer.xml");
        log.info("import to Concept_Answer Table  Successful");

        log.info("import  to Concept_Set Table  Starting");
        dataImporter.importData(metaDataFilePath+"concepts_and_drugs/Concept_Set.xml");
        log.info("import to Concept_Set Table  Successful");

        log.info("import  to Concept_Reference Table  Starting");
        dataImporter.importData(metaDataFilePath+"concepts_and_drugs/Concept_Reference.xml");
        log.info("import to Concept_Reference Table  Successful");

        log.info("import  of  Concept Modifications Starting");
        dataImporter.importData(metaDataFilePath+"concepts_and_drugs/Concept_Modifications.xml");
        log.info("import to Concept Modifications Table  Successful");

        log.info("import  of  Drugs  Starting");
        dataImporter.importData(metaDataFilePath+"appointment.xml");
        log.info("import of Drugs  Successful");

        log.info("import  of  ICD 11 concepts  Starting");
        dataImporter.importData(metaDataFilePath+"concepts_and_drugs/icd_11/icd_11_import_concept.xml");
        log.info("import of ICD 11 concepts  Successful");

        log.info("import  of  ICD 11 concept_name Starting");
        dataImporter.importData(metaDataFilePath+"concepts_and_drugs/icd_11/icd_11_import_concept_name.xml");
        log.info("import of ICD 11 concept_name  Successful");

        log.info("import  of  ICD 11 concept_reference Starting");
        dataImporter.importData(metaDataFilePath+"concepts_and_drugs/icd_11/icd_11_import_concept_reference.xml");
        log.info("import of ICD 11 concept_reference  Successful");

        log.info("import  of  ICD 11 concept_map Starting");
        dataImporter.importData(metaDataFilePath+"concepts_and_drugs/icd_11/icd_11_import_concept_map.xml");
        log.info("import of ICD 11 concept_map  Successful");

        log.info("import  of  ICD 11 cause_of_death_set Starting");
        dataImporter.importData(metaDataFilePath+"concepts_and_drugs/cause_of_death_set.xml");
        log.info("import of ICD 11 cause_of_death_set  Successful");

        log.info("Move Non ICD Coded Diagnosis");
        dataImporter.importData(metaDataFilePath+"concepts_and_drugs/icd_11/move_non_icd11-10-to-msc.xml");
        log.info("Move non coded ICD 11 Diagnosis");

        log.info("import  to Concept Table  Starting");
        dataImporter.importData(metaDataFilePath+"concepts_and_drugs/tools-2024/Concept.xml");
        log.info("import to Concept Table  Successful");

        log.info("import  to Concept Name Table  Starting");
        dataImporter.importData(metaDataFilePath+"concepts_and_drugs/tools-2024/Concept_Name.xml");
        log.info("import to Concept Name Table  Successful");

        log.info("import  to Concept_Description Table  Starting");
        dataImporter.importData(metaDataFilePath+"concepts_and_drugs/tools-2024/Concept_Description.xml");
        log.info("import to Concept_Description Table  Successful");

        log.info("import  to Concept_Numeric Table  Starting");
        dataImporter.importData(metaDataFilePath+"concepts_and_drugs/tools-2024/Concept_Numeric.xml");
        log.info("import to Concept_Numeric Table  Successful");

        log.info("import  to Concept_Answer Table  Starting");
        dataImporter.importData(metaDataFilePath+"concepts_and_drugs/tools-2024/Concept_Answer.xml");
        log.info("import to Concept_Answer Table  Successful");

        log.info("import  to Concept_Set Table  Starting");
        dataImporter.importData(metaDataFilePath+"concepts_and_drugs/tools-2024/Concept_Set.xml");
        log.info("import to Concept_Set Table  Successful");

        log.info("import  to Concept_Reference Table  Starting");
        dataImporter.importData(metaDataFilePath+"concepts_and_drugs/tools-2024/Concept_Reference.xml");
        log.info("import to Concept_Reference Table  Successful");

        log.info("Retire Meta data");
        dataImporter.importData(metaDataFilePath+"concepts_and_drugs/retire_meta_data.xml");
        log.info("Retiring of meta data is Successful");

        log.info("Start import of person attributes");
        dataImporter.importData(metaDataFilePath+"Person_Attribute_Types.xml");
        log.info("Person Attributes imported");

        log.info("Start import of UgandaEMR Privileges");
        dataImporter.importData(metaDataFilePath+"Role_Privilege.xml");
        log.info("UgandaEMR Privileges Imported");

        log.info("Start import of UgandaEMR Visits");
        dataImporter.importData(metaDataFilePath+"VisitTypes.xml");
        log.info("UgandaEMR Visits Imported");

        log.info("Start import of UgandaEMR Relationship Types");
        dataImporter.importData(metaDataFilePath+"RelationshipTypes.xml");
        log.info("UgandaEMR Relationship Types Imported");

        log.info("Start import of Program related objects");
        dataImporter.importData(metaDataFilePath+"Programs.xml");
        log.info(" Program related objects Imported");

        log.info("import  of  Drugs  Starting");
        dataImporter.importData(metaDataFilePath+"concepts_and_drugs/Drug.xml");
        log.info("import of Drugs  Successful");

        log.info("Start import of orderFrequencies related objects");
        dataImporter.importData(metaDataFilePath + "order_frequency.xml");
        log.info(" orderFrequencies related objects Imported");

        log.info("Start import of stock item objects");
        dataImporter.importData(metaDataFilePath + "stockmanagement/stock_item.xml");
        dataImporter.importData(metaDataFilePath + "stockmanagement/stock_item_packaging_uom.xml");
        dataImporter.importData(metaDataFilePath + "stockmanagement/stock_item_uom.xml");
        log.info("stock item  objects Imported");
    }

    /**
     * Generate patientIdentifier for old OpenMRS Migration to the new
     */

    protected PatientIdentifier generatePatientIdentifier() {
        IdentifierSourceService iss = Context.getService(IdentifierSourceService.class);
        IdentifierSource idSource = iss.getIdentifierSource(1); // this is the default OpenMRS identifier source
        PatientService patientService = Context.getPatientService();

        UUID uuid = UUID.randomUUID();

        PatientIdentifierType patientIdentifierType = patientService.getPatientIdentifierTypeByUuid("05a29f94-c0ed-11e2-94be-8c13b969e334");

        PatientIdentifier pid = new PatientIdentifier();
        pid.setIdentifierType(patientIdentifierType);
        String identifier = iss.generateIdentifier(idSource, "New OpenMRS ID with CheckDigit");
        pid.setIdentifier(identifier);
        pid.setPreferred(true);
        pid.setUuid(String.valueOf(uuid));

        return pid;

    }

    /**
     * Generate an OpenMRS ID for patients who do not have one due to a migration from an old OpenMRS ID to a new one which contains a check-digit
     **/
    public void generateOpenMRSIdentifierForPatientsWithout() {
        PatientService patientService = Context.getPatientService();
        AdministrationService as = Context.getAdministrationService();
        AlertService alertService = Context.getAlertService();

        List<List<Object>> patientIds = as.executeSQL("SELECT patient_id FROM patient_identifier WHERE patient_id NOT IN (SELECT patient_id FROM patient_identifier p INNER JOIN patient_identifier_type pt ON (p.identifier_type = pt.patient_identifier_type_id AND pt.uuid = '05a29f94-c0ed-11e2-94be-8c13b969e334'))", true);

        if (patientIds.size() == 0) {
            // no patients to process
            return;
        }
        // get the identifier source copied from RegistrationCoreServiceImpl

        for (List<Object> row : patientIds) {
            Patient p = patientService.getPatient((Integer) row.get(0));
            // Create new Patient Identifier
            PatientIdentifier pid = generatePatientIdentifier();
            pid.setPatient(p);
            try {
                log.info("Adding OpenMRS ID " + pid.getIdentifier() + " to patient with id " + p.getPatientId());
                // Save the patient Identifier
                patientService.savePatientIdentifier(pid);
            } catch (Exception e) {
                // log the error to the alert service but do not rethrow the exception since the module has to start
                log.error("Error updating OpenMRS identifier for patient #" + p.getPatientId(), e);
            }
        }
        log.info("All patients updated with new OpenMRS ID");
    }

    /**
     * Configure the global properties for the expected functionality
     *
     * @return
     */
    public void initializePrimaryIdentifierTypeMapping() {
        // The primary identifier type now uses metadata mapping instead of a global property
        MetadataMappingService metadataMappingService = Context.getService(MetadataMappingService.class);
        MetadataTermMapping primaryIdentifierTypeMapping = metadataMappingService.getMetadataTermMapping(
                EmrApiConstants.EMR_METADATA_SOURCE_NAME,
                EmrApiConstants.PRIMARY_IDENTIFIER_TYPE
        );
        PatientIdentifierType openmrsIdType = Context.getPatientService()
                .getPatientIdentifierTypeByUuid(PatientIdentifierTypes.NATIONAL_ID.uuid());

        // Overwrite if not set yet
        if (!openmrsIdType.getUuid().equals(primaryIdentifierTypeMapping.getMetadataUuid())) {
            primaryIdentifierTypeMapping.setMappedObject(openmrsIdType);
            metadataMappingService.saveMetadataTermMapping(primaryIdentifierTypeMapping);
        }
    }


    public void installCommonMetadata() {
        MetadataDeployService deployService=Context.getService(MetadataDeployService.class);
        try {
            log.info("Installing standard metadata using the packages.xml file");
            MetadataUtil.setupStandardMetadata(getClass().getClassLoader());
            log.info("Standard metadata installed");

            log.info("Installing metadata");
            log.info("Installing commonly used metadata");
            deployService.installBundle(Context.getRegisteredComponents(CommonMetadataBundle.class).get(0));
            log.info("Finished installing commonly used metadata");
            log.info("Installing address hierarchy");
            deployService.installBundle(Context.getRegisteredComponents(UgandaAddressMetadataBundle.class).get(0));
            log.info("Finished installing addresshierarchy");

            // install concepts
            log.info("Installing patient flags");
            deployService.installBundle(Context.getRegisteredComponents(UgandaEMRPatientFlagMetadataBundle.class).get(0));
            log.info("Finished installing patient flags");

        } catch (Exception e) {
            Module mod = ModuleFactory.getModuleById("ugandaemr");
            ModuleFactory.stopModule(mod);
            throw new RuntimeException("failed to install the common metadata ", e);
        }
    }

    public void removeOldChangeLocksForDataIntegrityModule() {
        String gpVal = Context.getAdministrationService().getGlobalProperty("dataintegrity.database_version");
        // remove data integrity locks for an version below 4
        // some gymnastics to get the major version number from semver like 2.5.3
        if ((gpVal == null) || new Integer(gpVal.substring(0, gpVal.indexOf("."))).intValue() < 4) {
            AdministrationService as = Context.getAdministrationService();
            log.warn("Removing liquibase change log locks for previously installed data integrity instance");
            as.executeSQL("delete from liquibasechangelog WHERE ID like 'dataintegrity%';", false);
        }
    }

    public List<Initializer> initialiseForms() {
        String jsonFormsPath = getMetadataPath("jsonforms") + "/";
        String htmlFormsPath = getMetadataPath("htmlforms");
        String initialiseExternalHTMLForm = Context.getAdministrationService().getGlobalProperty("ugandaemr.metadata.externalhtmlforms.initialize");
        String initialiseMetaDataOnStart = Context.getAdministrationService().getGlobalProperty("ugandaemr.initialiseMetadataOnStart");

        List<Initializer> l = new ArrayList<Initializer>();
        l.add(new AppConfigurationInitializer());

        if (Boolean.parseBoolean(initialiseMetaDataOnStart)) {
            l.add(new JsonFormsInitializer(UgandaEMRConstants.MODULE_ID, ""));
        } else {
            l.add(new JsonFormsInitializer(UgandaEMRConstants.MODULE_ID, jsonFormsPath));
        }

        if (Boolean.parseBoolean(initialiseExternalHTMLForm) && !Boolean.parseBoolean(initialiseMetaDataOnStart)) {
            l.add(new HtmlFormsInitializer(UgandaEMRConstants.MODULE_ID));
        }
        return l;
    }


    public void setHealthFacilityLocation(){
        LocationService locationService=Context.getLocationService();
        AdministrationService administrationService=Context.getAdministrationService();
        Location healthCenter = locationService.getLocationByUuid("629d78e9-93e5-43b0-ad8a-48313fd99117");
        healthCenter.setName(administrationService.getGlobalProperty(UgandaEMRConstants.GP_HEALTH_CENTER_NAME));
        locationService.saveLocation(healthCenter);
    }

    public  void  setFlagStatus(){
        AdministrationService administrationService=Context.getAdministrationService();
        String flagstatus = administrationService.getGlobalProperty("ugandaemr.patientflags.disabledFlags");

        if (flagstatus != null) {
            flagstatus = ("'" + flagstatus.trim().replace(",", "','") + "'").replace(",''", "").replace("' ", "'");
            administrationService.executeSQL("update patientflags_flag set enabled=0 where name in (" + flagstatus.trim() + ")", false);
        }
    }

    public void disableEnableAPPS(){
        AppFrameworkService appFrameworkService = Context.getService(AppFrameworkService.class);
        // disable the reference app registration page
        appFrameworkService.disableApp("referenceapplication.registrationapp.registerPatient");
        // disable the start visit app since all data is retrospective
        appFrameworkService.disableExtension("org.openmrs.module.coreapps.createVisit");
        // the extension to the edit person details
        appFrameworkService.disableExtension("org.openmrs.module.registrationapp.editPatientDemographics");

        // disable apps on the Clinican facing dashboard added through coreapps 1.12.0
        appFrameworkService.disableApp("coreapps.mostRecentVitals");
        appFrameworkService.disableApp("coreapps.diagnoses");
        appFrameworkService.disableApp("coreapps.latestObsForConceptList");
        appFrameworkService.disableApp("coreapps.obsAcrossEncounters");
        appFrameworkService.disableApp("coreapps.obsGraph");
        appFrameworkService.enableApp("coreapps.visitByEncounterType");
        appFrameworkService.disableApp("coreapps.dataIntegrityViolations");
        appFrameworkService.disableApp("fingerprint.findPatient");
        appFrameworkService.enableApp("ugandaemr.findPatient");
        appFrameworkService.disableApp("ugandaemr.registrationapp.registerPatient");

        // enable the relationships dashboard widget
        appFrameworkService.enableApp("coreapps.relationships");

        // Remove the BIRT reports app since it is no longer supported
        appFrameworkService.disableApp("ugandaemr.referenceapplication.birtReports");

        // Home page apps clean up
        appFrameworkService.disableApp("referenceapplication.vitals"); // Capture Vitals
        appFrameworkService.disableApp("coreapps.activeVisits"); // Active Visits

        // form entry app on the home page
        appFrameworkService.disableApp("xforms.formentry");
        // disable the default find patient app to provide one which allows searching for patients at the footer of the search for patients page
        appFrameworkService.disableApp("coreapps.findPatient");
        // form entry extension in active visits
        appFrameworkService.disableExtension("xforms.formentry.cfpd");
    }

    public CheckInPatient checkInPatient(Patient patient, Location currentLocation, Location locationTo, Location queueRoom, Provider provider, String visitComment, String patientStatus, String visitTypeUuid, Integer priority) {
        PatientQueue patientQueue = new PatientQueue();
        PatientQueueingService patientQueueingService = Context.getService(PatientQueueingService.class);

        if (patientStatus != null && patientStatus.equals("emergency")) {
            patientQueue.setPriority(priority == null ? 0 : priority);
            patientQueue.setPriorityComment(patientStatus);
        }


        if (visitComment != null) {
            patientQueue.setComment(visitComment);
        }

        Visit visit = createVisitForToday(patient, currentLocation.getParentLocation(), visitTypeUuid);
        patientQueue.setLocationFrom(currentLocation);
        patientQueue.setPatient(patient);
        patientQueue.setLocationTo(locationTo);
        patientQueue.setQueueRoom(queueRoom);
        patientQueue.setProvider(provider);
        patientQueue.setStatus(PatientQueue.Status.PENDING);
        patientQueue.setCreator(Context.getAuthenticatedUser());
        patientQueue.setDateCreated(new Date());
        patientQueueingService.assignVisitNumberForToday(patientQueue);
        patientQueueingService.savePatientQue(patientQueue);

        CheckInPatient checkInPatient = new CheckInPatient();

        checkInPatient.setPatientQueue(patientQueue);

        checkInPatient.setVisit(visit);

        return checkInPatient;
    }

    private Visit createVisitForToday(Patient patient, Location location, String visitTypeUuid) {
        VisitService visitService = Context.getVisitService();
        List<Visit> visitList = Context.getVisitService().getActiveVisitsByPatient(patient);
        Visit todayVisit = null;

        VisitType visitType = visitService.getVisitTypeByUuid(visitTypeUuid);

        if (visitList.isEmpty()) {
            AdtService adtService = Context.getService(AdtService.class);
            Visit visit = adtService.ensureVisit(patient, new Date(), location);
            if (visitType != null) {
                visit.setVisitType(visitType);
                try {
                    visitService.saveVisit(visit);
                } catch (Exception var14) {
                    log.error(var14);
                }
            }
        } else {
            for (Visit visit : visitList) {
                Date largestEncounterDate = OpenmrsUtil.getLastMomentOfDay(visit.getStartDatetime());
                for (Encounter encounter : visit.getEncounters()) {
                    if (encounter.getEncounterDatetime().after(largestEncounterDate)) {
                        largestEncounterDate = encounter.getEncounterDatetime();
                    }
                }

                if (!visit.getStartDatetime().after(OpenmrsUtil.firstSecondOfDay(new Date()))) {
                    Context.getVisitService().endVisit(visit, largestEncounterDate);
                } else {
                    todayVisit = visit;
                }
            }

            if (todayVisit == null) {
                AdtService adtService = Context.getService(AdtService.class);
                Visit visit = adtService.ensureVisit(patient, new Date(), location);
                if (visitType != null) {
                    visit.setVisitType(visitType);
                    try {
                        visitService.saveVisit(visit);
                    } catch (Exception var14) {
                        log.error(var14);
                    }
                }
            }
        }

        return todayVisit;
    }

    public void copyFilesToApplicationDataDirectory(String source, String destination) {
        final ResourceFactory resourceFactory = ResourceFactory.getInstance();
        final ResourceProvider resourceProvider = resourceFactory.getResourceProviders().get(UgandaEMRConstants.MODULE_ID);

        // Scanning the forms resources folder
        final File formsDir = resourceProvider.getResource(source);
        if (formsDir == null || !formsDir.isDirectory()) {
            log.error("No files could be retrieved from the provided folder: " + UgandaEMRConstants.MODULE_ID + ":" + source);
            return;
        }

        copyDirectoryRecursively(formsDir, Paths.get(getMetadataPath(destination)));
    }

    private void copyDirectoryRecursively(File sourceDir, Path destinationDir) {
        try {
            Files.createDirectories(destinationDir); // Ensure destination directory exists

            for (File file : sourceDir.listFiles()) {
                Path targetPath = destinationDir.resolve(file.getName()); // Maintain directory structure

                if (file.isDirectory()) {
                    // Recursively copy subdirectories
                    copyDirectoryRecursively(file, targetPath);
                } else {
                    // Copy files
                    try (InputStream inputStream = new FileInputStream(file)) {
                        Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
                        log.info("File copied successfully to " + targetPath);
                    }
                }
            }
        } catch (IOException e) {
            log.error("Error copying directory: " + e.getMessage());
        }
    }

    public void downloadFormsAndMetaDataFromGitHub() {
        String repoOwner = Context.getAdministrationService().getGlobalProperty("ugandaemr.metadata.github.organization");
        String repoName = Context.getAdministrationService().getGlobalProperty("ugandaemr.metadata.github.reponame");
        String branch = Context.getAdministrationService().getGlobalProperty("ugandaemr.metadata.github.branch.metadata");
        String folderToCopy = Context.getAdministrationService().getGlobalProperty("ugandaemr.metadata.github.metadata.directory");
        String destinationRoot = getMetadataPath("configuration");

        try {
            downloadAndExtractFolder(repoOwner, repoName, branch, folderToCopy, destinationRoot);
            log.info("Folder copied successfully with directory structure preserved!");
            initializeMetaData();
        } catch (IOException e) {
            log.info("Error: " + e.getMessage());
        }
    }

    public void downloadFrontendFromGitHub() {
        String repoOwner = Context.getAdministrationService().getGlobalProperty("ugandaemr.metadata.github.organization");
        String repoName = Context.getAdministrationService().getGlobalProperty("ugandaemr.metadata.github.reponame");
        String branch = Context.getAdministrationService().getGlobalProperty("ugandaemr.metadata.github.branch.frontend");
        String folderToCopy = Context.getAdministrationService().getGlobalProperty("ugandaemr.metadata.github.frontend.directory");
        String destinationRoot = getMetadataPath("frontend");

        try {
            downloadAndExtractFolder(repoOwner, repoName, branch, folderToCopy, destinationRoot);
            log.info("Folder copied successfully with directory structure preserved!");
        } catch (IOException e) {
            log.info("Error: " + e.getMessage());
        }
    }

    public void downloadOmodsFromGitHub() {
        String repoOwner = Context.getAdministrationService().getGlobalProperty("ugandaemr.metadata.github.organization");
        String repoName = Context.getAdministrationService().getGlobalProperty("ugandaemr.metadata.github.reponame");
        String branch = Context.getAdministrationService().getGlobalProperty("ugandaemr.metadata.github.branch.modules");
        String folderToCopy = Context.getAdministrationService().getGlobalProperty("ugandaemr.metadata.github.modules.directory");
        String destinationRoot = getMetadataPath("modules");

        try {
            downloadAndExtractFolder(repoOwner, repoName, branch, folderToCopy, destinationRoot);
            log.info("Folder copied successfully with directory structure preserved!");
        } catch (IOException e) {
            log.info("Error: " + e.getMessage());
        }
    }

    public void downloadAndExtractFolder(String owner, String repo, String branch, String folder, String destination) throws IOException {
        String zipUrl = "https://github.com/" + owner + "/" + repo + "/archive/refs/heads/" + branch + ".zip";
        String zipPath = destination + "/repo.zip";
        String extractPath = destination + "/extracted/";
        String extractedFolder = extractPath + repo + "-" + branch + "/" + folder;

        // Step 1: Download ZIP
        downloadZipFile(zipUrl, zipPath);

        // Step 2: Extract ZIP
        extractZipFile(zipPath, extractPath);

        // Step 3: Load omod prefixes from JSON
        Path sourcePath = Paths.get(extractedFolder);
        List<String> omodPrefixes = loadOmodPrefixesFromJson(sourcePath.resolve("omod-prefixes.json"));

        // Step 4: Delete 'modules' folder if applicable
        Path destinationPath = Paths.get(destination, folder);
        if (shouldDeleteFolder(folder, destinationPath)) {
            deleteDirectory(destinationPath);
        }
        Files.createDirectories(destinationPath);

        // Step 5: Copy and replace files
        copyFilesWithReplacement(sourcePath, destinationPath, omodPrefixes);

        // Step 6: Cleanup
        cleanupTemporaryFiles(zipPath, extractPath);
    }

    private void downloadZipFile(String zipUrl, String zipPath) throws IOException {
        log.info("⬇️ Downloading ZIP from: " + zipUrl);
        try (InputStream in = new URL(zipUrl).openStream()) {
            Files.copy(in, Paths.get(zipPath), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void extractZipFile(String zipPath, String extractPath) throws IOException {
        unzip(zipPath, extractPath);
    }

    private List<String> loadOmodPrefixesFromJson(Path jsonPath) throws IOException {
        if (Files.exists(jsonPath)) {
            log.info(String.format("📄 Reading prefix list from: %s",jsonPath));
            ObjectMapper mapper = new ObjectMapper();
            return Arrays.asList(mapper.readValue(jsonPath.toFile(), String[].class));
        } else {
            log.info(String.format("⚠️ Prefix list not found at: %s",jsonPath));
            return new ArrayList<>();
        }
    }

    private boolean shouldDeleteFolder(String folder, Path destinationPath) throws IOException {
        return !"modules".equalsIgnoreCase(folder) && Files.exists(destinationPath);
    }

    private void copyFilesWithReplacement(Path sourcePath, Path destinationPath, List<String> omodPrefixes) throws IOException {
        Files.walk(sourcePath).forEach(source -> {
            Path relativePath = sourcePath.relativize(source);
            Path dest = destinationPath.resolve(relativePath);
            try {
                if (Files.isDirectory(source)) {
                    Files.createDirectories(dest);
                } else {
                    String fileName = source.getFileName().toString();
                    if (fileName.endsWith(".omod")) {
                        for (String prefix : omodPrefixes) {
                            if (fileName.startsWith(prefix)) {
                                try (DirectoryStream<Path> stream = Files.newDirectoryStream(destinationPath.getParent())) {
                                    for (Path existingFile : stream) {
                                        String existingName = existingFile.getFileName().toString();
                                        if (existingName.startsWith(prefix) && existingName.endsWith(".omod")) {
                                            log.info("🗑️ Deleting old OMOD: " + existingFile);
                                            Files.deleteIfExists(existingFile);
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                    Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
                    log.info("📄 Copied: " + dest);
                }
            } catch (IOException e) {
                log.info("❌ Error copying file: " + source + " -> " + dest + ": " + e.getMessage());
            }
        });
    }

    private void cleanupTemporaryFiles(String zipPath, String extractPath) throws IOException {
        Files.deleteIfExists(Paths.get(zipPath));
        deleteDirectory(Paths.get(extractPath));
    }



    private void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) destDir.mkdirs();
        log.info("📂 Extracting ZIP to: " + destDirectory);

        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry = zipIn.getNextEntry();
            while (entry != null) {
                File file = new File(destDirectory, entry.getName());

                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    // Ensure parent directories exist
                    file.getParentFile().mkdirs();

                    // Overwrite if file already exists
                    if (file.exists()) {
                        file.delete(); // Delete existing file before writing new one
                    }

                    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
                        byte[] bytes = new byte[4096];
                        int read;
                        while ((read = zipIn.read(bytes)) != -1) {
                            bos.write(bytes, 0, read);
                        }
                    }
                }

                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        }
    }


    private void deleteDirectory(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walk(path)
                    .sorted((p1, p2) -> p2.compareTo(p1)) // Delete children first
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

}
