package org.openmrs.module.ugandaemr.tasks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Visit;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientqueueing.api.PatientQueueingService;
import org.openmrs.module.patientqueueing.model.PatientQueue;
import org.openmrs.module.ugandaemr.api.UgandaEMRService;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.openmrs.util.OpenmrsUtil;

import java.util.Date;
import java.util.List;

public class StopActiveFacilityVisitTask extends AbstractTask {
    protected final Log log = LogFactory.getLog(this.getClass());

    @Override
    public void execute() {
        VisitService visitService = Context.getVisitService();
        PatientQueueingService patientQueueingService = Context.getService(PatientQueueingService.class);
        List<Visit> visitList = visitService.getAllVisits();
        List<PatientQueue> incompleteQueues = Context.getService(PatientQueueingService.class).getPatientQueueList(null, null, OpenmrsUtil.firstSecondOfDay(new Date()), null, null, null, PatientQueue.Status.PENDING);
        incompleteQueues = Context.getService(PatientQueueingService.class).getPatientQueueList(null, null, OpenmrsUtil.firstSecondOfDay(new Date()), null, null, null, PatientQueue.Status.PICKED);

        for (PatientQueue patientQueue : incompleteQueues) {
            try {
                patientQueueingService.completePatientQueue(patientQueue);
            } catch (Exception e) {
                log.error(e);
            }
        }

        for (Visit visit : visitList) {
            if (visit.getStopDatetime() == null && visit.getVisitType().getUuid().equals("7b0f5697-27e3-40c4-8bae-f4049abfb4ed") && visit.getStartDatetime().before(OpenmrsUtil.firstSecondOfDay(new Date()))) {
                try {
                    Date largestEncounterDate = OpenmrsUtil.getLastMomentOfDay(visit.getStartDatetime());
                    for (Encounter encounter : visit.getEncounters()) {
                        if (encounter.getEncounterDatetime().after(largestEncounterDate)) {
                            largestEncounterDate = OpenmrsUtil.getLastMomentOfDay(encounter.getEncounterDatetime());
                        }
                    }
                    visitService.endVisit(visit, largestEncounterDate);
                } catch (Exception e) {
                    log.error(e);
                }
            }
        }
    }
}
