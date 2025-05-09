/*
package org.openmrs.module.ugandaemr.htmlformentry;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;


import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ugandaemr.metadata.core.Programs;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.web.test.jupiter.BaseModuleWebContextSensitiveTest;;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

@Ignore
public class MCHProgramExitPostSubmissionActionTest extends BaseModuleWebContextSensitiveTest {
	
	protected static final String UGANDAEMR_STANDARD_DATASET_XML = "org/openmrs/module/ugandaemr/include/standardTestDataset.xml";
	
	private String xml = "<htmlform>\n"
			+ "Date: <encounterDate default='today'/>\n"
			+ "Location: <encounterLocation default='1'/>\n"
			+ "Provider: <encounterProvider role='Provider' />\n"	
			+ "<obs conceptId=\"4\" />\n"
			+ "<postSubmissionAction class='org.openmrs.module.ugandaemr.htmlformentry.MCHProgramExitPostSubmissionAction'/>\n"
			+ "<submit/>"
			+ "</htmlform>";
	
	@BeforeEach
	public void setup() throws Exception {
		executeDataSet(UGANDAEMR_STANDARD_DATASET_XML);
	}
	
	@AfterEach
	public void cleanup() throws Exception {
		deleteAllData();
	}
	
	@Test
	public void shouldExitPatientFromMCHProgramWhenNewPNCFormIsSubmitted() throws Exception {
		Patient patient = new Patient(8);
		ProgramWorkflowService service = Context.getService(ProgramWorkflowService.class);
		Program mchProgram = service.getProgramByUuid(Programs.MCH_PROGRAM.uuid());
		
		//should be enrolled in the mch program
		List<PatientProgram> patientPrograms = service.getPatientPrograms(patient, mchProgram, null, null, null, null, false);
		assertEquals(1, patientPrograms.size());
		assertNull(patientPrograms.get(0).getDateCompleted());
			
		//prepare and submit a new  html form to exit patient from mch program
		HtmlForm htmlForm = new HtmlForm();
		htmlForm.setXmlData(xml);
		Form form = new Form(1);
		form.setEncounterType(new EncounterType(1));
		htmlForm.setForm(form);
		FormEntrySession session = new FormEntrySession(patient, null, FormEntryContext.Mode.ENTER, htmlForm, new MockHttpSession());
        
        //getHtmlToDisplay() is called to generate necessary tag handlers and cache the form
        session.getHtmlToDisplay();
        
        //prepareForSubmit is called to set patient and encounter if specified in tags
        session.prepareForSubmit();
        
        HttpServletRequest request = mock(MockHttpServletRequest.class);
        when(request.getParameter("w1")).thenReturn("2017-04-01");
        session.getSubmissionController().handleFormSubmission(session, request);
        
        session.applyActions();
        
        //should not be enrolled in mch program
        patientPrograms = service.getPatientPrograms(patient, mchProgram, null, null, null, null, false);
		assertEquals(1, patientPrograms.size());
		assertNotNull(patientPrograms.get(0).getDateCompleted());
	}
	
	@Test
	public void shouldEnrollAndExitPatientFromMCHProgramWhenNewPNCFormIsSubmittedForNonEnrolledPatient() throws Exception {
		Patient patient = new Patient(2);
		ProgramWorkflowService service = Context.getService(ProgramWorkflowService.class);
		Program mchProgram = service.getProgramByUuid(Programs.MCH_PROGRAM.uuid());
		
		//should not be enrolled in the mch program and should not even have a voided enrollment
		List<PatientProgram> patientPrograms = service.getPatientPrograms(patient, mchProgram, null, null, null, null, false);
		assertEquals(0, patientPrograms.size());
			
		//prepare and submit a new  html form to exit patient from mch program
		HtmlForm htmlForm = new HtmlForm();
		htmlForm.setXmlData(xml);
		Form form = new Form(1);
		form.setEncounterType(new EncounterType(1));
		htmlForm.setForm(form);
		FormEntrySession session = new FormEntrySession(patient, null, FormEntryContext.Mode.ENTER, htmlForm, new MockHttpSession());
        
        //getHtmlToDisplay() is called to generate necessary tag handlers and cache the form
        session.getHtmlToDisplay();
        
        //prepareForSubmit is called to set patient and encounter if specified in tags
        session.prepareForSubmit();
        
        HttpServletRequest request = mock(MockHttpServletRequest.class);
        when(request.getParameter("w1")).thenReturn("2017-04-01");
        session.getSubmissionController().handleFormSubmission(session, request);
        
        session.applyActions();
        
        //should not be enrolled in mch program
        patientPrograms = service.getPatientPrograms(patient, mchProgram, null, null, null, null, false);
		assertEquals(1, patientPrograms.size());
		assertNotNull(patientPrograms.get(0).getDateCompleted());
	}
	
	@Test
	public void shouldNotExitPatientFromMCHProgramWhenEditedPNCFormIsSubmitted() throws Exception {
		Patient patient = new Patient(8);
		ProgramWorkflowService service = Context.getService(ProgramWorkflowService.class);
		Program mchProgram = service.getProgramByUuid(Programs.MCH_PROGRAM.uuid());
		
		//should be enrolled in the mch program
		List<PatientProgram> patientPrograms = service.getPatientPrograms(patient, mchProgram, null, null, null, null, false);
		assertEquals(1, patientPrograms.size());
		assertNull(patientPrograms.get(0).getDateCompleted());
		
		//prepare and submit an edited html form to see if we shall exit patient from mch program
		HtmlForm htmlForm = new HtmlForm();
		htmlForm.setXmlData(xml);
		Form form = new Form(1);
		form.setEncounterType(new EncounterType(1));
		htmlForm.setForm(form);
		Encounter encounter = new Encounter();
		encounter.setDateCreated(new Date());
		encounter.setEncounterDatetime(new Date());
		FormEntrySession session = new FormEntrySession(patient, encounter, FormEntryContext.Mode.EDIT, htmlForm, new MockHttpSession());
        
        //getHtmlToDisplay() is called to generate necessary tag handlers and cache the form
        session.getHtmlToDisplay();
        
        //prepareForSubmit is called to set patient and encounter if specified in tags
        session.prepareForSubmit();
        
        HttpServletRequest request = mock(MockHttpServletRequest.class);
        when(request.getParameter("w1")).thenReturn("2017-04-01");
        session.getSubmissionController().handleFormSubmission(session, request);
        
        session.applyActions();
        
        //should still be enrolled in mch program
        patientPrograms = service.getPatientPrograms(patient, mchProgram, null, null, null, null, false);
		assertEquals(1, patientPrograms.size());
		assertNull(patientPrograms.get(0).getDateCompleted());
	}
}
*/
