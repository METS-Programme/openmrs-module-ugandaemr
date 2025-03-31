package org.openmrs.module.ugandaemr.htmlformentry;


import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.CustomFormSubmissionAction;
import org.openmrs.module.htmlformentry.FormEntryContext.Mode;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.patientqueueing.model.PatientQueue;
import org.openmrs.module.ugandaemr.api.UgandaEMRService;

import java.util.Date;
import java.util.Set;

import static org.openmrs.module.ugandaemr.UgandaEMRConstants.CONCEPT_ID_NEXT_APPOINTMENT;
import static org.openmrs.module.ugandaemr.UgandaEMRConstants.CONCEPT_ID_TRANSFERED_OUT;

/**
 * Post form submission for patient
 */
public class ViralLoadSubmissionAction implements CustomFormSubmissionAction {

    private Encounter orderViralLoadTest(FormEntrySession formEntrySession) {
        Obs viralLoadTest = null;
        Obs labNo = null;
        Obs specimenSource = null;
        Encounter encounter = null;
        for (Obs obs : formEntrySession.getEncounter().getAllObs()) {
            if (obs.getValueCoded() != null && obs.getValueCoded().getConceptId() == 165412) {
                viralLoadTest = obs;
            } else if (obs.getConcept().getConceptId() == 165845) {
                labNo = obs;
            } else if (obs.getConcept().getConceptId() == 159959) {
                specimenSource = obs;
            }
        }

        if (viralLoadTest != null && labNo != null && specimenSource != null) {
            encounter = Context.getService(UgandaEMRService.class).processRetrospectiveViralLoadOrder(viralLoadTest, labNo, specimenSource);
        }
        return encounter;
    }

    @Override
    public void applyAction(FormEntrySession session) {

        Mode mode = session.getContext().getMode();
        if (!(mode.equals(Mode.ENTER) || mode.equals(Mode.EDIT))) {
            return;
        }
        orderViralLoadTest(session);

    }
}
