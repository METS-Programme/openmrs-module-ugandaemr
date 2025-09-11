package org.openmrs.module.ugandaemr;

import org.junit.Test;

/**
 * Test for generating the UIC task for patients without the UIC
 */
import java.util.Date;
import java.util.List;


import org.junit.Before;
import org.openmrs.*;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ugandaemr.api.UgandaEMRService;
import org.openmrs.test.BaseModuleContextSensitiveTest;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class UgandaEMRServiceTest extends BaseModuleContextSensitiveTest {

    protected static final String UGANDAEMR_STANDARD_DATASET_XML = "org/openmrs/module/ugandaemr/include/standardTestDataset.xml";


    protected UgandaEMRService ugandaemrService;
    protected PatientService patientService;
    protected VisitService visitService;
    protected AdministrationService administrationService;

    @Before
    public void setup() throws Exception {
        executeDataSet(UGANDAEMR_STANDARD_DATASET_XML);
        ugandaemrService = Context.getService(UgandaEMRService.class);
        patientService = Context.getPatientService();
        visitService = Context.getVisitService();
        administrationService = Context.getAdministrationService();
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


}