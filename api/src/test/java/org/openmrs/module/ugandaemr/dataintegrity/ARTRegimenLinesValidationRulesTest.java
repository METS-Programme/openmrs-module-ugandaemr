package org.openmrs.module.ugandaemr.dataintegrity;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.module.dataintegrity.rule.RuleResult;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ARTRegimenLinesValidationRulesTest extends BaseModuleContextSensitiveTest {

	protected static final String UGANDAEMR_STANDARD_DATASET_XML = "org/openmrs/module/ugandaemr/include/standardTestDataset.xml";

	ARTRegimenLinesValidationRules artRegimenLinesValidationRules;

	@Before
	public void initialize() throws Exception {
		executeDataSet(UGANDAEMR_STANDARD_DATASET_XML);
		artRegimenLinesValidationRules = new ARTRegimenLinesValidationRules();
	}

	@Test
	public void testPatientsWithUnknownRegimenLine() {
		List<RuleResult<Patient>> result = artRegimenLinesValidationRules.patientsWithUnknownRegimenLine();
		assertNotNull(result);
		assertEquals(1, result.size());
		Patient patient = result.get(0).getEntity();
		assertEquals(7, patient.getId().longValue());
	}
}