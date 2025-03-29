/*
package org.openmrs.module.ugandaemr.htmlformentry;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Program;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.ugandaemr.metadata.core.Programs;
import org.openmrs.web.test.jupiter.BaseModuleWebContextSensitiveTest;;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

*/
/**
 * Tests patient enrollment into the MCH program
 *//*

public class ARTEnrollmentSubmissionActionTest extends BaseModuleWebContextSensitiveTest {

    protected static final String UGANDAEMR_DSDM_DATASET_XML = "org/openmrs/module/ugandaemr/include/dsdmTestDataSet.xml";

    private String xml = "<htmlform formEncounterType=\"8d5b27bc-c2cc-11de-8d13-0010c6dffd0f\">\n" + "" + "Date: <encounterDate default='today'/>\n" + "Location: <encounterLocation default='1'/>\n" + "Provider: <encounterProvider role='Provider' default='1'/>\n" + "<obs id=\"165143\" conceptId=\"165143\" answerConceptIds=\"165138,165140,165139,165142,165141\" answerLabels=\"FBIM,FBG,FTR,CDDP,CCLAD\" required=\"required\" labelText=\"DSDM Model\" defaultValue=\"165140\"/>\n" + "<postSubmissionAction class='org.openmrs.module.ugandaemr.htmlformentry.ARTEnrollmentSubmissionAction'/>\n" + "<submit/>" + "</htmlform>";

    private String xml2 = "<htmlform formEncounterType=\"8d5b27bc-c2cc-11de-8d13-0010c6dffd0f\">\n" + "" + "Date: <encounterDate default='2015-02-10 00:00:00'/>\n" + "Location: <encounterLocation default='1'/>\n" + "Provider: <encounterProvider role='Provider' default='2'/>\n" + "<input type=\"hidden\" name=\"personId\" value=\"1393\"/>" + "<input type=\"hidden\" name=\"createVisit\" value=\"false\"/>\n" + "<input type=\"hidden\" name=\"encounterId\" value=\"31182\"/>\n" + "<input type=\"hidden\" name=\"visitId\" value=\"31165\"/>\n" + "<obs id=\"165143\" conceptId=\"165143\" answerConceptIds=\"165138,165140,165139,165142,165141\" answerLabels=\"FBIM,FBG,FTR,CDDP,CCLAD\" required=\"required\" labelText=\"DSDM Model\" defaultValue=\"165140\"/>\n" + "<postSubmissionAction class='org.openmrs.module.ugandaemr.htmlformentry.ARTEnrollmentSubmissionAction'/>\n" + "<submit/>" + "</htmlform>";

    @BeforeEach
    public void setup() throws Exception {
        executeDataSet(UGANDAEMR_DSDM_DATASET_XML);
    }

    @AfterEach
    public void cleanup() throws Exception {
        deleteAllData();
    }

    */
/**
     * New  Patient Encounter should enroll patient in first line regimen
     *
     * @throws Exception
     *//*

    @Test
    public void shouldAssignPatientToFirstLineRegimenStateOnSubmit() throws Exception {
        Patient patient = new Patient(1393);
        ProgramWorkflowService service = Context.getService(ProgramWorkflowService.class);
        Program hivProgram = service.getProgramByUuid(Programs.HIV_PROGRAM.uuid());

        //prepare and submit an html form to enroll patient in mch program
        HtmlForm htmlForm1 = new HtmlForm();
        htmlForm1.setXmlData(xml);
        Form form1 = new Form(8);
        form1.setEncounterType(Context.getEncounterService().getEncounterType(8));
        htmlForm1.setForm(form1);
        FormEntrySession session1 = new FormEntrySession(patient, null, FormEntryContext.Mode.ENTER, htmlForm1, new MockHttpSession());

        //getHtmlToDisplay() is called to generate necessary tag handlers and cache the form
        session1.getHtmlToDisplay();

        //prepareForSubmit is called to set patient and encounter if specified in tags
        session1.prepareForSubmit();

        HttpServletRequest request = mock(MockHttpServletRequest.class);
        when(request.getParameter("w1")).thenReturn("2021-07-29");
        session1.getSubmissionController().handleFormSubmission(session1, request);

        session1.applyActions();

        List<PatientProgram> patientPrograms = service.getPatientPrograms(patient, hivProgram, null, null, null, null, false);
        assertEquals(1, patientPrograms.size());
        assertEquals(Programs.HIV_PROGRAM.uuid(), patientPrograms.get(0).getProgram().getUuid());
        assertNull(patientPrograms.get(0).getDateCompleted());
        assertEquals(1, patientPrograms.get(0).getStates().size());
        PatientState firstLineRegimenState = patientPrograms.get(0).getStates().iterator().next();
        assertEquals(90271, firstLineRegimenState.getState().getConcept().getConceptId().intValue());
    }

    */
/**
     * New  Patient Encounter with same DSDM Model as the current DSDM Model
     *
     * @throws Exception
     *//*

    @Test
    public void shouldEnrollPatientInDSDMProgramWhenDSDMIsSelectedOnNewForm() throws Exception {
        Patient patient = new Patient(1393);
        ProgramWorkflowService service = Context.getService(ProgramWorkflowService.class);

        //prepare and submit an html form to enroll patient in mch program
        HtmlForm htmlForm1 = new HtmlForm();
        htmlForm1.setXmlData(xml);
        Form form1 = new Form(8);
        form1.setEncounterType(Context.getEncounterService().getEncounterType(15));
        htmlForm1.setForm(form1);
        FormEntrySession session1 = new FormEntrySession(patient, null, FormEntryContext.Mode.ENTER, htmlForm1, new MockHttpSession());


        //getHtmlToDisplay() is called to generate necessary tag handlers and cache the form
        session1.getHtmlToDisplay();

        //prepareForSubmit is called to set patient and encounter if specified in tags
        session1.prepareForSubmit();

        HttpServletRequest request = mock(MockHttpServletRequest.class);
        when(request.getParameter("w1")).thenReturn("2017-04-01");
        //Seleted DSDM Program in form is FTR
        when(request.getParameter("w8")).thenReturn("165139");
        session1.getSubmissionController().handleFormSubmission(session1, request);

        session1.applyActions();
        List<PatientProgram> patientProgramList = getPatientPrograms(patient, null, null, null, false);
        assertEquals(service.getProgramByName("FTR"), patientProgramList.get(1).getProgram());
        assertEquals(service.getProgramByName("FBIM"), patientProgramList.get(0).getProgram());
        assertEquals(session1.getEncounter().getEncounterDatetime(), patientProgramList.get(1).getDateEnrolled());
        assertEquals(patientProgramList.get(1).getDateEnrolled(), patientProgramList.get(0).getDateCompleted());

    }

    */
/**
     * New Encounter Patient same date as the Current DSDM
     *
     * @throws Exception
     *//*

    @Test
    public void shouldNotEnrollPatientInDSDMProgramWhenDSDMIsSelectedOnNewForm() throws Exception {
        Patient patient = new Patient(1393);
        ProgramWorkflowService service = Context.getService(ProgramWorkflowService.class);

        //prepare and submit an html form to enroll patient in mch program
        HtmlForm htmlForm1 = new HtmlForm();
        htmlForm1.setXmlData(xml);
        Form form1 = new Form(8);
        form1.setEncounterType(Context.getEncounterService().getEncounterType(15));
        htmlForm1.setForm(form1);
        FormEntrySession session1 = new FormEntrySession(patient, null, FormEntryContext.Mode.ENTER, htmlForm1, new MockHttpSession());


        //getHtmlToDisplay() is called to generate necessary tag handlers and cache the form
        session1.getHtmlToDisplay();

        //prepareForSubmit is called to set patient and encounter if specified in tags
        session1.prepareForSubmit();

        HttpServletRequest request = mock(MockHttpServletRequest.class);
        when(request.getParameter("w1")).thenReturn("2015-02-10 00:00:00");
        //Seleted DSDM Program in concept
        when(request.getParameter("w8")).thenReturn("165138");
        session1.getSubmissionController().handleFormSubmission(session1, request);

        session1.applyActions();
        //Patient DSDM Patient Program Should remain active
        assertTrue(service.getPatientProgram(2069).getActive());
        assertEquals(service.getProgramByName("FBIM"), getPatientPrograms(patient, null, null, null, false).get(0).getProgram());
        assertTrue(service.getPatientPrograms(Context.getPatientService().getPatient(1393), null, null, null, null, null, true).size() == 1);
    }


    */
/**
     * Changing DSDM Model on same day that is enrolled
     *
     * @throws Exception
     *//*

    @Test
    public void shouldEnrollPatientInDSDMProgramWhenDSDMIsSelectedOnEdit() throws Exception {
        Patient patient = new Patient(1393);
        ProgramWorkflowService service = Context.getService(ProgramWorkflowService.class);

        //prepare and submit an html form to enroll patient in mch program
        HtmlForm htmlForm1 = new HtmlForm();
        htmlForm1.setXmlData(xml2);
        Form form1 = new Form(8);
        form1.setEncounterType(Context.getEncounterService().getEncounterType(15));
        htmlForm1.setForm(form1);
        Encounter encounter = new Encounter();
        encounter.setDateCreated(new Date());
        encounter.setEncounterDatetime(new Date());
        FormEntrySession session1 = new FormEntrySession(patient, encounter, FormEntryContext.Mode.EDIT, htmlForm1, new MockHttpSession());


        //getHtmlToDisplay() is called to generate necessary tag handlers and cache the form8session1.getHtmlToDisplay();
        System.out.println(session1.getHtmlToDisplay());

        //prepareForSubmit is called to set patient and encounter if specified in tags
        session1.prepareForSubmit();

        HttpServletRequest request = mock(MockHttpServletRequest.class);
        //Same Date as Current DSDM Program FBIM
        when(request.getParameter("w1")).thenReturn("2015-02-10 00:00:00");
        //Seleted DSDM Program in form is CCLAD
        when(request.getParameter("w8")).thenReturn("165141");
        session1.getSubmissionController().handleFormSubmission(session1, request);

        session1.applyActions();
        List<PatientProgram> patientProgramList = getPatientPrograms(patient, null, null, null, false);
        assertEquals(service.getProgramByName("CCLAD"), patientProgramList.get(0).getProgram());
        assertTrue(patientProgramList.size() == 1);
        assertTrue(getPatientPrograms(Context.getPatientService().getPatient(1393), null, null, null, true).size() == 2);
        assertTrue(patientProgramList.get(0).getActive());
        assertEquals(session1.getEncounter().getEncounterDatetime(), patientProgramList.get(0).getDateEnrolled());
    }

    */
/**
     * No DSDM Selected on form on edit when there was a dsdm model enrolled on the same date as the encounter date
     *
     * @throws Exception
     *//*

    @Test
    public void shouldUnEnrollPatientInDSDMProgramWhenDSDMIsSelectedOnEdit() throws Exception {
        Patient patient = new Patient(1393);
        ProgramWorkflowService service = Context.getService(ProgramWorkflowService.class);

        //prepare and submit an html form to enroll patient in mch program
        HtmlForm htmlForm1 = new HtmlForm();
        htmlForm1.setXmlData(xml2);
        Form form1 = new Form(8);
        form1.setEncounterType(Context.getEncounterService().getEncounterType(15));
        htmlForm1.setForm(form1);
        Encounter encounter = new Encounter();
        encounter.setDateCreated(new Date());
        encounter.setEncounterDatetime(new Date());
        FormEntrySession session1 = new FormEntrySession(patient, encounter, FormEntryContext.Mode.EDIT, htmlForm1, new MockHttpSession());


        //getHtmlToDisplay() is called to generate necessary tag handlers and cache the form8session1.getHtmlToDisplay();
        System.out.println(session1.getHtmlToDisplay());

        //prepareForSubmit is called to set patient and encounter if specified in tags
        session1.prepareForSubmit();

        HttpServletRequest request = mock(MockHttpServletRequest.class);
        //DSDM Date same as current DSDM Date for FBIM
        when(request.getParameter("w1")).thenReturn("2015-02-10 00:00:00");
        //No DSDM on same date where FBIM was enrolled
        when(request.getParameter("w8")).thenReturn("");
        session1.getSubmissionController().handleFormSubmission(session1, request);

        session1.applyActions();
        List<PatientProgram> patientProgramList = getPatientPrograms(patient, null, null, null, false);
        assertTrue(patientProgramList.size() == 0);
        assertTrue(getPatientPrograms(patient, null, null, null, true).size() == 1);
    }

    */
/**
     * Same DSDM MOdel as current DSDM model on later date.
     *
     * @throws Exception
     *//*

    @Test
    public void shouldIgnoreEnrollmentWhenDSDMIsSelectedOnEdit() throws Exception {
        Patient patient = new Patient(1393);
        ProgramWorkflowService service = Context.getService(ProgramWorkflowService.class);

        //prepare and submit an html form to enroll patient in mch program
        HtmlForm htmlForm1 = new HtmlForm();
        htmlForm1.setXmlData(xml2);
        Form form1 = new Form(8);
        form1.setEncounterType(Context.getEncounterService().getEncounterType(15));
        htmlForm1.setForm(form1);
        Encounter encounter = new Encounter();
        encounter.setDateCreated(new Date());
        encounter.setEncounterDatetime(new Date());
        FormEntrySession session1 = new FormEntrySession(patient, encounter, FormEntryContext.Mode.EDIT, htmlForm1, new MockHttpSession());


        //getHtmlToDisplay() is called to generate necessary tag handlers and cache the form8session1.getHtmlToDisplay();
        System.out.println(session1.getHtmlToDisplay());

        //prepareForSubmit is called to set patient and encounter if specified in tags
        session1.prepareForSubmit();

        HttpServletRequest request = mock(MockHttpServletRequest.class);
        //Different Date from the cdate
        when(request.getParameter("w1")).thenReturn("2017-02-10 00:00:00");
        //Same as Current DSDM MOdel where FBIM was enrolled
        when(request.getParameter("w8")).thenReturn("165138");
        session1.getSubmissionController().handleFormSubmission(session1, request);

        session1.applyActions();
        assertTrue(getPatientPrograms(patient, null, null, null, false).size() == 1);
        assertTrue(getPatientPrograms(patient, null, null, null, false).get(0).getActive());
    }

    private List<PatientProgram> getPatientPrograms(Patient patient, Program program, Date minEnrollmentDate, Date maxEnrollmentDate, boolean includeVoided) {
        List<PatientProgram> patientPrograms = new ArrayList<PatientProgram>();

        patientPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient, program, minEnrollmentDate, maxEnrollmentDate, null, null, includeVoided);
        return patientPrograms;
    }

}
*/
