/*
package org.openmrs.module.ugandaemr.tasks;

import java.util.List;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.api.context.Context;
import org.openmrs.web.test.jupiter.BaseModuleWebContextSensitiveTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

*/
/**
 * Tests the task to link infants to mothers, builds on @link MotherToBabyPairLinkingPostSubmissionActionTest
 *//*

public class LinkExposedInfantToMotherTaskTest extends BaseModuleWebContextSensitiveTest {
	LinkExposedInfantToMotherTask task;
	
	@BeforeEach
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/ugandaemr/include/standardTestDataset.xml");
		executeDataSet("org/openmrs/module/ugandaemr/include/exposedInfantData.xml");
		
		updateSearchIndex();
	}
	
	@Test
	public void shouldlinkExposedInfantToMother() {
		task = new LinkExposedInfantToMotherTask();
		task.execute();
		
		Person infant = Context.getPersonService().getPerson(10005);
		List<Relationship> parents = Context.getPersonService().getRelationshipsByPerson(infant);
		
		
		assertEquals( 1, parents.size(),"Infant linked to mother via ART number");
		assertEquals(7, parents.get(0).getPersonA().getPersonId().longValue() ,"Mother ID is 7");
		assertEquals("8d91a210-c2cc-11de-8d13-0010c6dffd0f", parents.get(0).getRelationshipType().getUuid());
	}
}
*/
