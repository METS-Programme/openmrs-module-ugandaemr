/*
package org.openmrs.module.ugandaemr.fragment;


import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Patient;
import org.openmrs.module.ugandaemr.fragment.controller.PatientStabilityFragmentController;
import org.openmrs.module.ugandaemr.web.resource.StabilityCriteriaResource;
import org.openmrs.web.test.jupiter.BaseModuleWebContextSensitiveTest;


import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Ignore
public class PatientStabilityFragmentControllerTest extends BaseModuleWebContextSensitiveTest {

    protected static final String UGANDAEMR_DSDM_DATASET_XML = "org/openmrs/module/ugandaemr/include/dsdmTestDataSet.xml";

    @AfterEach
    public void setup() throws Exception {
        executeDataSet(UGANDAEMR_DSDM_DATASET_XML);
    }

    @AfterEach
    public void cleanup() throws Exception {
        deleteAllData();
    }

    @Test
    public void testDateSubtraction() {
        StabilityCriteriaResource stabilityCriteriaResource = new StabilityCriteriaResource();

        stabilityCriteriaResource.getDateBefore(new Date(), -12, 0);
    }

    @Test
    public void testGetArtStartDate() {
        Patient patient = new Patient(1393);
        StabilityCriteriaResource stabilityCriteriaResource = new StabilityCriteriaResource();
        Date artStartDate = stabilityCriteriaResource.getArtStartDate(patient);
        assertNotNull(artStartDate);
        assertEquals(artStartDate.toString(), "2013-02-06 00:00:00.0");
    }
}
*/
