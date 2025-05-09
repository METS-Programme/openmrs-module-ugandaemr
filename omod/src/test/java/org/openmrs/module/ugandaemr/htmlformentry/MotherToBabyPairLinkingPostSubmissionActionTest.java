/*
package org.openmrs.module.ugandaemr.htmlformentry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Encounter;
import org.openmrs.Relationship;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.FormSubmissionActions;
import org.openmrs.web.test.jupiter.BaseModuleWebContextSensitiveTest;;

*/
/**
 * Test linking of mothers to HIV exposed infants through the mother's ART number
 *//*

public class MotherToBabyPairLinkingPostSubmissionActionTest extends BaseModuleWebContextSensitiveTest {
	private FormEntrySession mockFormEntrySession;
	
	private FormEntryContext mockFormEntryContext;
	
	private FormSubmissionActions mockFormSubmissionActions;
	
	private MotherToBabyPairLinkingPostSubmissionAction motherToBabyPairLinkingPostSubmissionAction;
	
	private PatientService mockPatientService;
	
	@BeforeEach
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/ugandaemr/include/standardTestDataset.xml");
		executeDataSet("org/openmrs/module/ugandaemr/include/exposedInfantData.xml");
		
		updateSearchIndex();
		
		mockFormEntrySession = mock(FormEntrySession.class);
		mockFormEntryContext = mock(FormEntryContext.class);
		mockFormSubmissionActions = mock(FormSubmissionActions.class);
		mockPatientService = mock(PatientService.class);
		motherToBabyPairLinkingPostSubmissionAction = new MotherToBabyPairLinkingPostSubmissionAction();
		
		when(mockFormEntrySession.getSubmissionActions()).thenReturn(mockFormSubmissionActions);
		when(mockFormEntrySession.getContext()).thenReturn(mockFormEntryContext);
		when(mockFormEntrySession.getContext().getMode()).thenReturn(FormEntryContext.Mode.ENTER);
		
	}
	
	@Test
	public void shouldLinkBabyToMotherWithValidARTNumber() throws Exception {
		Encounter e = Context.getEncounterService().getEncounter(10005);
		when(mockFormEntrySession.getEncounter()).thenReturn(e);
		
		motherToBabyPairLinkingPostSubmissionAction.applyAction(mockFormEntrySession);
		
		List<Relationship> parents = Context.getPersonService().getRelationshipsByPerson(e.getPatient().getPerson());
		
		
		assertEquals(1, parents.size(),"Infant linked to mother via ART number");
		assertEquals(7, parents.get(0).getPersonA().getPersonId().longValue(),"Mother ID is 7");
		assertEquals( parents.get(0).getRelationshipType().getUuid(),"8d91a210-c2cc-11de-8d13-0010c6dffd0f");
	}
	
	@Test
	public void shouldNotLinkBabyToMotherAgainIfAlreadyDone() throws Exception {
		Encounter e = Context.getEncounterService().getEncounter(10012);
		when(mockFormEntrySession.getEncounter()).thenReturn(e);
		
		motherToBabyPairLinkingPostSubmissionAction.applyAction(mockFormEntrySession);
		
		List<Relationship> parents = Context.getPersonService().getRelationshipsByPerson(e.getPatient().getPerson());
		
		
		assertEquals(parents.size(), 1,"Infant already linked should not link again");
		assertEquals(8, parents.get(0).getPersonA().getPersonId().longValue(),"Mother ID is 8");
		assertEquals("8d91a210-c2cc-11de-8d13-0010c6dffd0f", parents.get(0).getRelationshipType().getUuid());
	}
	
	@Test
	public void shouldNotLinkBabyIfARTNumberBelongsToMan() throws Exception {
		Encounter e = Context.getEncounterService().getEncounter(10010);
		when(mockFormEntrySession.getEncounter()).thenReturn(e);
		
		motherToBabyPairLinkingPostSubmissionAction.applyAction(mockFormEntrySession);
		
		List<Relationship> parents = Context.getPersonService().getRelationshipsByPerson(e.getPatient().getPerson());
		
		
		assertEquals(parents.size(), 0,"Infant with linked to man as mother");
	}
	
	@Test
	public void shouldNotLinkBabyIfARTNumberIsForPersonBelow12Years() throws Exception {
		Encounter e = Context.getEncounterService().getEncounter(10006);
		when(mockFormEntrySession.getEncounter()).thenReturn(e);
		
		motherToBabyPairLinkingPostSubmissionAction.applyAction(mockFormEntrySession);
		
		List<Relationship> parents = Context.getPersonService().getRelationshipsByPerson(e.getPatient().getPerson());
		
		
		assertEquals(parents.size(), 0,"Infant with mother below 12 years");
	}
	
	@Test
	public void shouldNotLinkBabyIfARTNumberIsNotEntered() throws Exception {
		Encounter e = Context.getEncounterService().getEncounter(10001);
		when(mockFormEntrySession.getEncounter()).thenReturn(e);
		
		motherToBabyPairLinkingPostSubmissionAction.applyAction(mockFormEntrySession);
		
		List<Relationship> parents = Context.getPersonService().getRelationshipsByPerson(e.getPatient().getPerson());
		
		
		assertEquals(parents.size(), 0,"Infant with no mother ART number should not be linked");
	}
	
	@Test
	public void shouldNotLinkBabyIfARTNumberDoesNotBelongToAnyPatientInSystem() throws Exception {
		Encounter e = Context.getEncounterService().getEncounter(10003);
		when(mockFormEntrySession.getEncounter()).thenReturn(e);
		
		motherToBabyPairLinkingPostSubmissionAction.applyAction(mockFormEntrySession);
		
		List<Relationship> parents = Context.getPersonService().getRelationshipsByPerson(e.getPatient().getPerson());
		
		
		assertEquals(parents.size(), 0,"Infant with ART number not belonging to anyone in the system should not be linked");
	}
	
}
*/
